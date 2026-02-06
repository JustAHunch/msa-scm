package com.logistics.scm.oms.inventory.domain.inbound.entity;

/**
 * 입고 상태
 */
public enum InboundStatus {
    REQUESTED,    // 입고 신청
    INSPECTING,   // 검수 중
    APPROVED,     // 합격 (검수 통과)
    REJECTED,     // 불합격 (반송)
    COMPLETED     // 입고 완료 (재고 반영 완료)
}
