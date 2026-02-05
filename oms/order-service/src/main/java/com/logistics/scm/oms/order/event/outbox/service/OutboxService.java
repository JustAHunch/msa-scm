package com.logistics.scm.oms.order.event.outbox.service;

import com.logistics.scm.oms.order.event.outbox.entity.Outbox;

/**
 * Outbox Service 인터페이스
 */
public interface OutboxService {

    /**
     * Outbox 이벤트 저장
     *
     * @param aggregateType 집계 타입
     * @param aggregateId 집계 ID
     * @param eventType 이벤트 타입
     * @param payload 이벤트 페이로드 (JSON)
     * @return 저장된 Outbox
     */
    Outbox saveOutbox(String aggregateType, String aggregateId, 
                      String eventType, String payload);

    /**
     * Outbox 발행 성공 처리
     *
     * @param outbox Outbox
     */
    void markAsPublished(Outbox outbox);

    /**
     * Outbox 발행 실패 처리
     *
     * @param outbox Outbox
     * @param errorMessage 오류 메시지
     */
    void markAsFailed(Outbox outbox, String errorMessage);
}
