package com.logistics.scm.oms.order.event.outbox.repository;

import com.logistics.scm.oms.order.event.outbox.entity.Outbox;
import com.logistics.scm.oms.order.event.outbox.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Outbox Repository
 */
@Repository
public interface OutboxRepository extends JpaRepository<Outbox, UUID> {

    /**
     * 발행 대기 중인 이벤트 조회
     * 생성 시각 오름차순 정렬 (오래된 것부터 처리)
     *
     * @return 발행 대기 중인 Outbox 목록
     */
    List<Outbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status);

    /**
     * 발행 실패한 이벤트 중 재시도 가능한 것 조회
     * 마지막 시도 시각이 특정 시간 이전인 것만 조회 (재시도 간격 보장)
     *
     * @param status 상태
     * @param before 마지막 시도 시각 기준
     * @return 재시도 대상 Outbox 목록
     */
    List<Outbox> findByStatusAndLastAttemptAtBeforeOrderByCreatedAtAsc(
            OutboxStatus status, LocalDateTime before);

    /**
     * 발행 완료된 오래된 이벤트 조회 (정리용)
     *
     * @param status 상태
     * @param before 발행 완료 시각 기준
     * @return 정리 대상 Outbox 목록
     */
    List<Outbox> findByStatusAndPublishedAtBefore(OutboxStatus status, LocalDateTime before);
}
