package com.logistics.scm.oms.order.event.outbox.entity;

/**
 * Outbox 상태 Enum
 */
public enum OutboxStatus {
    /**
     * 발행 대기
     */
    PENDING,

    /**
     * 발행 완료
     */
    PUBLISHED,

    /**
     * 발행 실패 (재시도 가능)
     */
    FAILED
}
