package com.logistics.scm.oms.inventory.domain.inbound.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.scm.oms.inventory.domain.inbound.dto.request.InboundCreateRequest;
import com.logistics.scm.oms.inventory.domain.inbound.dto.request.InboundInspectionRequest;
import com.logistics.scm.oms.inventory.domain.inbound.dto.response.InboundResponse;
import com.logistics.scm.oms.inventory.domain.inbound.entity.Inbound;
import com.logistics.scm.oms.inventory.domain.inbound.entity.InboundStatus;
import com.logistics.scm.oms.inventory.domain.inbound.exception.InboundNotFoundException;
import com.logistics.scm.oms.inventory.domain.inbound.repository.InboundRepository;
import com.logistics.scm.oms.inventory.domain.inventory.service.InventoryService;
import com.logistics.scm.oms.inventory.event.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 입고 서비스 구현체
 *
 * SERVICE_FLOW "2.2 허브 입고" 프로세스 구현
 * - 입고 신청 → 검수 시작 → 합격(재고 등록) / 불합격(반송)
 * - Outbox Pattern으로 이벤트 발행
 *
 * @author c.h.jo
 * @since 2026-02-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InboundServiceImpl implements InboundService {

    private final InboundRepository inboundRepository;
    private final InventoryService inventoryService;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    @Override
    @Transactional
    public InboundResponse createInbound(InboundCreateRequest request) {
        log.info("입고 신청: warehouseId={}, productCode={}, qty={}, companyId={}",
                request.getWarehouseId(), request.getProductCode(),
                request.getRequestedQty(), request.getCompanyId());

        // 입고 번호 생성
        String inboundNumber = generateInboundNumber();

        Inbound inbound = Inbound.builder()
                .inboundNumber(inboundNumber)
                .warehouseId(request.getWarehouseId())
                .productCode(request.getProductCode())
                .productName(request.getProductName())
                .requestedQty(request.getRequestedQty())
                .companyId(request.getCompanyId())
                .status(InboundStatus.REQUESTED)
                .remarks(request.getRemarks())
                .build();

        Inbound savedInbound = inboundRepository.save(inbound);

        // hub.inbound_requested 이벤트 Outbox 저장
        try {
            String payload = objectMapper.writeValueAsString(InboundResponse.from(savedInbound));
            outboxService.saveOutbox(
                    "Inbound",
                    savedInbound.getId().toString(),
                    "InboundRequestedEvent",
                    payload
            );
        } catch (Exception e) {
            log.error("입고 신청 이벤트 Outbox 저장 실패: inboundId={}", savedInbound.getId(), e);
        }

        log.info("입고 신청 완료: inboundId={}, inboundNumber={}",
                savedInbound.getId(), savedInbound.getInboundNumber());

        return InboundResponse.from(savedInbound);
    }

    @Override
    @Transactional(readOnly = true)
    public InboundResponse getInboundById(UUID inboundId) {
        log.debug("입고 조회: inboundId={}", inboundId);

        Inbound inbound = inboundRepository.findById(inboundId)
                .orElseThrow(() -> new InboundNotFoundException(inboundId));

        return InboundResponse.from(inbound);
    }

    @Override
    @Transactional
    public InboundResponse startInspection(UUID inboundId, String inspectorId) {
        log.info("검수 시작: inboundId={}, inspectorId={}", inboundId, inspectorId);

        Inbound inbound = inboundRepository.findById(inboundId)
                .orElseThrow(() -> new InboundNotFoundException(inboundId));

        inbound.startInspection(inspectorId);
        Inbound savedInbound = inboundRepository.save(inbound);

        log.info("검수 시작 완료: inboundId={}", savedInbound.getId());

        return InboundResponse.from(savedInbound);
    }

    @Override
    @Transactional
    public InboundResponse completeInspection(UUID inboundId, InboundInspectionRequest request) {
        log.info("검수 완료 처리: inboundId={}, approved={}", inboundId, request.getApproved());

        Inbound inbound = inboundRepository.findById(inboundId)
                .orElseThrow(() -> new InboundNotFoundException(inboundId));

        // 검수 시작 상태가 아니면 자동으로 검수 시작
        if (inbound.getStatus() == InboundStatus.REQUESTED) {
            inbound.startInspection(request.getInspectorId());
        }

        if (request.getApproved()) {
            // ===== 합격 처리 =====
            Integer actualQty = request.getActualQty() != null
                    ? request.getActualQty()
                    : inbound.getRequestedQty();

            inbound.approve(actualQty);

            // 재고 등록/증가 (InventoryService 연동)
            inventoryService.createOrUpdateInventory(
                    inbound.getWarehouseId(),
                    inbound.getProductCode(),
                    actualQty,
                    request.getSafetyStock()
            );

            // 입고 완료 상태로 변경
            inbound.complete();

            // hub.inbound_completed 이벤트 Outbox 저장
            try {
                String payload = objectMapper.writeValueAsString(InboundResponse.from(inbound));
                outboxService.saveOutbox(
                        "Inbound",
                        inbound.getId().toString(),
                        "InboundCompletedEvent",
                        payload
                );
            } catch (Exception e) {
                log.error("입고 완료 이벤트 Outbox 저장 실패: inboundId={}", inbound.getId(), e);
            }

            log.info("입고 합격 처리 완료: inboundId={}, actualQty={}, productCode={}",
                    inbound.getId(), actualQty, inbound.getProductCode());

        } else {
            // ===== 불합격 처리 =====
            inbound.reject(request.getRejectionReason());

            // hub.inbound_rejected 이벤트 Outbox 저장
            try {
                String payload = objectMapper.writeValueAsString(InboundResponse.from(inbound));
                outboxService.saveOutbox(
                        "Inbound",
                        inbound.getId().toString(),
                        "InboundRejectedEvent",
                        payload
                );
            } catch (Exception e) {
                log.error("입고 거부 이벤트 Outbox 저장 실패: inboundId={}", inbound.getId(), e);
            }

            log.info("입고 불합격 처리 완료: inboundId={}, reason={}",
                    inbound.getId(), request.getRejectionReason());
        }

        Inbound savedInbound = inboundRepository.save(inbound);
        return InboundResponse.from(savedInbound);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundResponse> getInboundsByStatus(InboundStatus status) {
        log.debug("상태별 입고 조회: status={}", status);

        return inboundRepository.findByStatus(status).stream()
                .map(InboundResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundResponse> getInboundsByWarehouse(UUID warehouseId) {
        log.debug("창고별 입고 조회: warehouseId={}", warehouseId);

        return inboundRepository.findByWarehouseId(warehouseId).stream()
                .map(InboundResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 입고 번호 생성 (IB-yyyyMMdd-SEQ)
     */
    private String generateInboundNumber() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = SEQUENCE.getAndIncrement();
        return String.format("IB-%s-%09d", dateStr, seq);
    }
}
