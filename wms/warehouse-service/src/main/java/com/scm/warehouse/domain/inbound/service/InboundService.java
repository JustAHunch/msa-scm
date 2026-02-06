package com.scm.warehouse.domain.inbound.service;

import com.scm.warehouse.domain.inbound.dto.request.InboundCreateRequest;
import com.scm.warehouse.domain.inbound.dto.response.InboundResponse;
import com.scm.warehouse.entity.InboundOrder.InboundStatus;
import com.scm.warehouse.entity.InboundOrder.InboundType;

import java.util.List;
import java.util.UUID;

/**
 * InboundService - 입고 서비스 인터페이스
 * 
 * 입고 타입:
 * - IB (Purchase Inbound): 일반 입고 (외부 업체 → 우리 창고)
 * - TI (Transfer Inbound): 허브 간 이동 입고 (A 창고 → B 창고)
 * 
 * @author c.h.jo
 * @since 2026-02-06
 */
public interface InboundService {

    /**
     * 입고 주문 생성 (IB, TI 타입 모두 지원)
     * 
     * @param request 입고 생성 요청
     * @return 생성된 입고 정보
     */
    InboundResponse createInbound(InboundCreateRequest request);

    /**
     * 입고 승인 (입고 시작)
     * 
     * @param inboundId 입고 ID
     * @return 업데이트된 입고 정보
     */
    InboundResponse approveInbound(UUID inboundId);

    /**
     * 입고 완료
     * - IB: inventory.increased 이벤트 발행
     * - TI: inventory.increased 이벤트 발행 (목적지 허브 재고 증가)
     * 
     * @param inboundId 입고 ID
     * @return 완료된 입고 정보
     */
    InboundResponse completeInbound(UUID inboundId);

    /**
     * 입고 거부 (품질 불량)
     * 
     * @param inboundId 입고 ID
     * @param reason 거부 사유
     * @return 업데이트된 입고 정보
     */
    InboundResponse rejectInbound(UUID inboundId, String reason);

    /**
     * 입고 주문 조회
     * 
     * @param inboundId 입고 ID
     * @return 입고 정보
     */
    InboundResponse getInbound(UUID inboundId);

    /**
     * 입고 번호로 조회
     * 
     * @param inboundNumber 입고 번호
     * @return 입고 정보
     */
    InboundResponse getInboundByNumber(String inboundNumber);

    /**
     * 창고별 입고 목록 조회
     * 
     * @param warehouseId 창고 ID
     * @return 입고 목록
     */
    List<InboundResponse> getInboundsByWarehouse(UUID warehouseId);

    /**
     * 창고 + 상태별 입고 목록 조회
     * 
     * @param warehouseId 창고 ID
     * @param status 입고 상태
     * @return 입고 목록
     */
    List<InboundResponse> getInboundsByWarehouseAndStatus(UUID warehouseId, InboundStatus status);

    /**
     * 창고 + 타입별 입고 목록 조회
     * 
     * @param warehouseId 창고 ID
     * @param type 입고 타입
     * @return 입고 목록
     */
    List<InboundResponse> getInboundsByWarehouseAndType(UUID warehouseId, InboundType type);
}
