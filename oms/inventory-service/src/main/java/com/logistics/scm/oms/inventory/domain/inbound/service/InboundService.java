package com.logistics.scm.oms.inventory.domain.inbound.service;

import com.logistics.scm.oms.inventory.domain.inbound.dto.request.InboundCreateRequest;
import com.logistics.scm.oms.inventory.domain.inbound.dto.request.InboundInspectionRequest;
import com.logistics.scm.oms.inventory.domain.inbound.dto.response.InboundResponse;
import com.logistics.scm.oms.inventory.domain.inbound.entity.InboundStatus;

import java.util.List;
import java.util.UUID;

/**
 * 입고 서비스 인터페이스
 *
 * SERVICE_FLOW "2.2 허브 입고" 프로세스
 * - 입고 신청 → 검수 시작 → 합격/불합격 → 재고 반영(합격 시)
 */
public interface InboundService {

    /**
     * 입고 신청
     *
     * @param request 입고 신청 요청
     * @return 입고 응답
     */
    InboundResponse createInbound(InboundCreateRequest request);

    /**
     * 입고 조회 (ID)
     *
     * @param inboundId 입고 ID
     * @return 입고 응답
     */
    InboundResponse getInboundById(UUID inboundId);

    /**
     * 입고 검수 시작
     *
     * @param inboundId 입고 ID
     * @param inspectorId 검수자 ID
     * @return 입고 응답
     */
    InboundResponse startInspection(UUID inboundId, String inspectorId);

    /**
     * 입고 검수 완료 (합격/불합격 처리)
     * - 합격 시: 재고 증가 이벤트 발행 + 재고 등록
     * - 불합격 시: 반송 처리
     *
     * @param inboundId 입고 ID
     * @param request 검수 결과 요청
     * @return 입고 응답
     */
    InboundResponse completeInspection(UUID inboundId, InboundInspectionRequest request);

    /**
     * 상태별 입고 목록 조회
     *
     * @param status 입고 상태
     * @return 입고 목록
     */
    List<InboundResponse> getInboundsByStatus(InboundStatus status);

    /**
     * 창고별 입고 목록 조회
     *
     * @param warehouseId 창고 ID
     * @return 입고 목록
     */
    List<InboundResponse> getInboundsByWarehouse(UUID warehouseId);
}
