package com.logistics.scm.oms.inventory.domain.stockmovement.entity;

/**
 * 재고 이동 유형
 */
public enum StockMovementType {
    INBOUND,        // 입고
    OUTBOUND,       // 출고
    ADJUST,         // 재고 조정
    RESERVED,       // 예약 (주문 생성 시)
    RELEASED,       // 예약 해제 (주문 취소 시)
    TRANSFER_OUT,   // 허브 간 이동 출고
    TRANSFER_IN,    // 허브 간 이동 입고
    HOLD,           // 보류 (불량/파손)
    RELEASE_HOLD,   // 보류 해제
    DISCARD         // 폐기
}
