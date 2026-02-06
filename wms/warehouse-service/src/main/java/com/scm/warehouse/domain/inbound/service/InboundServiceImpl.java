package com.scm.warehouse.domain.inbound.service;

import com.scm.warehouse.domain.inbound.dto.request.InboundCreateRequest;
import com.scm.warehouse.domain.inbound.dto.response.InboundResponse;
import com.scm.warehouse.domain.inbound.repository.InboundOrderRepository;
import com.scm.warehouse.entity.InboundItem;
import com.scm.warehouse.entity.InboundOrder;
import com.scm.warehouse.entity.InboundOrder.InboundStatus;
import com.scm.warehouse.entity.InboundOrder.InboundType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * InboundService 구현체
 * 
 * 입고 관리 핵심 비즈니스 로직
 * - IB (Purchase Inbound): 일반 입고 (외부 업체 → 우리 창고)
 * - TI (Transfer Inbound): 허브 간 이동 입고 (A 창고 → B 창고)
 * 
 * @author c.h.jo
 * @since 2026-02-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InboundServiceImpl implements InboundService {

    private final InboundOrderRepository inboundOrderRepository;

    @Override
    @Transactional
    public InboundResponse createInbound(InboundCreateRequest request) {
        log.info("입고 주문 생성 시작: warehouseId={}, type={}", 
                request.getWarehouseId(), request.getInboundType());

        // 입고 번호 생성
        String inboundNumber = generateInboundNumber(request.getInboundType());

        // InboundOrder 엔티티 생성
        InboundOrder inboundOrder = new InboundOrder();
        inboundOrder.setInboundNumber(inboundNumber);
        inboundOrder.setWarehouseId(request.getWarehouseId());
        inboundOrder.setInboundType(request.getInboundType());
        inboundOrder.setInboundStatus(InboundStatus.SCHEDULED);
        inboundOrder.setSupplierName(request.getSupplierName());
        inboundOrder.setExpectedDate(request.getExpectedDate());

        // InboundItem 엔티티 생성
        List<InboundItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    InboundItem item = new InboundItem();
                    item.setInboundOrder(inboundOrder);
                    item.setProductCode(itemRequest.getProductCode());
                    item.setExpectedQty(itemRequest.getExpectedQty());
                    item.setReceivedQty(0);
                    item.setLocationId(itemRequest.getLocationId());
                    item.setQcStatus(InboundItem.QcStatus.PENDING);
                    return item;
                })
                .collect(Collectors.toList());

        inboundOrder.setInboundItems(items);

        // 저장
        InboundOrder savedOrder = inboundOrderRepository.save(inboundOrder);

        log.info("입고 주문 생성 완료: inboundNumber={}, itemCount={}", 
                inboundNumber, items.size());

        return InboundResponse.from(savedOrder);
    }

    @Override
    @Transactional
    public InboundResponse approveInbound(UUID inboundId) {
        log.info("입고 승인 시작: inboundId={}", inboundId);

        InboundOrder inboundOrder = inboundOrderRepository.findById(inboundId)
                .orElseThrow(() -> new IllegalArgumentException("입고 주문을 찾을 수 없습니다: " + inboundId));

        // 상태 검증
        if (inboundOrder.getInboundStatus() != InboundStatus.SCHEDULED) {
            throw new IllegalStateException("예정 상태의 입고만 승인할 수 있습니다.");
        }

        // 상태 변경: SCHEDULED → RECEIVING
        inboundOrder.setInboundStatus(InboundStatus.RECEIVING);

        InboundOrder savedOrder = inboundOrderRepository.save(inboundOrder);

        log.info("입고 승인 완료: inboundNumber={}", savedOrder.getInboundNumber());

        return InboundResponse.from(savedOrder);
    }

    @Override
    @Transactional
    public InboundResponse completeInbound(UUID inboundId) {
        log.info("입고 완료 처리 시작: inboundId={}", inboundId);

        InboundOrder inboundOrder = inboundOrderRepository.findById(inboundId)
                .orElseThrow(() -> new IllegalArgumentException("입고 주문을 찾을 수 없습니다: " + inboundId));

        // 상태 검증
        if (inboundOrder.getInboundStatus() != InboundStatus.RECEIVING) {
            throw new IllegalStateException("입고 중 상태의 주문만 완료할 수 있습니다.");
        }

        // 상태 변경: RECEIVING → COMPLETED
        inboundOrder.setInboundStatus(InboundStatus.COMPLETED);
        inboundOrder.setReceivedDate(LocalDateTime.now());

        // TODO: Kafka 이벤트 발행
        // - IB 타입: hub.inbound_completed → inventory.increased
        // - TI 타입: hub.transfer_completed → inventory.increased (목적지 허브)

        InboundOrder savedOrder = inboundOrderRepository.save(inboundOrder);

        log.info("입고 완료: inboundNumber={}, type={}", 
                savedOrder.getInboundNumber(), savedOrder.getInboundType());

        return InboundResponse.from(savedOrder);
    }

    @Override
    @Transactional
    public InboundResponse rejectInbound(UUID inboundId, String reason) {
        log.info("입고 거부 처리 시작: inboundId={}, reason={}", inboundId, reason);

        InboundOrder inboundOrder = inboundOrderRepository.findById(inboundId)
                .orElseThrow(() -> new IllegalArgumentException("입고 주문을 찾을 수 없습니다: " + inboundId));

        // 상태 검증
        if (inboundOrder.getInboundStatus() == InboundStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 입고는 거부할 수 없습니다.");
        }

        // 상태 변경: → REJECTED
        inboundOrder.setInboundStatus(InboundStatus.REJECTED);

        // TODO: Kafka 이벤트 발행
        // hub.inbound_rejected → 반송 처리 (ReturnOrder 생성)

        InboundOrder savedOrder = inboundOrderRepository.save(inboundOrder);

        log.warn("입고 거부 완료: inboundNumber={}, reason={}", 
                savedOrder.getInboundNumber(), reason);

        return InboundResponse.from(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public InboundResponse getInbound(UUID inboundId) {
        log.debug("입고 조회: inboundId={}", inboundId);

        InboundOrder inboundOrder = inboundOrderRepository.findById(inboundId)
                .orElseThrow(() -> new IllegalArgumentException("입고 주문을 찾을 수 없습니다: " + inboundId));

        return InboundResponse.from(inboundOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public InboundResponse getInboundByNumber(String inboundNumber) {
        log.debug("입고 번호로 조회: inboundNumber={}", inboundNumber);

        InboundOrder inboundOrder = inboundOrderRepository.findByInboundNumber(inboundNumber)
                .orElseThrow(() -> new IllegalArgumentException("입고 주문을 찾을 수 없습니다: " + inboundNumber));

        return InboundResponse.from(inboundOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundResponse> getInboundsByWarehouse(UUID warehouseId) {
        log.debug("창고별 입고 목록 조회: warehouseId={}", warehouseId);

        return inboundOrderRepository.findByWarehouseId(warehouseId).stream()
                .map(InboundResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundResponse> getInboundsByWarehouseAndStatus(UUID warehouseId, InboundStatus status) {
        log.debug("창고 + 상태별 입고 목록 조회: warehouseId={}, status={}", warehouseId, status);

        return inboundOrderRepository.findByWarehouseIdAndInboundStatus(warehouseId, status).stream()
                .map(InboundResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundResponse> getInboundsByWarehouseAndType(UUID warehouseId, InboundType type) {
        log.debug("창고 + 타입별 입고 목록 조회: warehouseId={}, type={}", warehouseId, type);

        return inboundOrderRepository.findByWarehouseIdAndInboundType(warehouseId, type).stream()
                .map(InboundResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 입고 번호 생성
     * 형식: {TYPE}-{YYYYMMDD}-{SEQ}
     * 예: IB-20260206-0001, TI-20260206-0001
     */
    private String generateInboundNumber(InboundType type) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = type.name();
        
        // TODO: 실제로는 Redis 또는 DB 시퀀스를 사용하여 순번 관리
        String seq = String.format("%04d", (int) (Math.random() * 10000));
        
        return String.format("%s-%s-%s", prefix, dateStr, seq);
    }
}
