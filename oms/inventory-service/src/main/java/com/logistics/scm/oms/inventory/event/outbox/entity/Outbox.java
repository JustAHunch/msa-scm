package com.logistics.scm.oms.inventory.event.outbox.entity;

import com.logistics.scm.oms.inventory.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Outbox Entity
 * 
 * Transactional Outbox Pattern 구현
 * 비즈니스 트랜잭션과 이벤트 발행을 원자적으로 처리
 */
@Entity
@Table(name = "outbox_tb", indexes = {
        @Index(name = "idx_outbox_status", columnList = "status"),
        @Index(name = "idx_outbox_created_at", columnList = "createdAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Outbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "outbox_id", updatable = false, nullable = false)
    private UUID outboxId;

    /**
     * 집계 타입 (Inventory 등)
     */
    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    /**
     * 집계 ID (재고 ID 등)
     */
    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    /**
     * 이벤트 타입 (InventoryReservedEvent 등)
     */
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    /**
     * 이벤트 페이로드 (JSON 형식)
     */
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    /**
     * 발행 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status;

    /**
     * 발행 시도 횟수
     */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    /**
     * 마지막 발행 시도 시각
     */
    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    /**
     * 발행 완료 시각
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /**
     * 오류 메시지
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // ===== 비즈니스 메서드 =====

    /**
     * 발행 성공 처리
     */
    public void markAsPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    /**
     * 발행 실패 처리
     */
    public void markAsFailed(String errorMessage) {
        this.status = OutboxStatus.FAILED;
        this.retryCount++;
        this.lastAttemptAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    /**
     * 재시도 대상인지 확인
     * 최대 3회까지 재시도
     */
    public boolean canRetry() {
        return this.retryCount < 3 && this.status == OutboxStatus.PENDING;
    }

    /**
     * 발행 대기 상태로 재설정
     */
    public void resetToPending() {
        this.status = OutboxStatus.PENDING;
        this.errorMessage = null;
    }

    // ===== Getter 별칭 =====
    
    public UUID getId() {
        return outboxId;
    }

    /**
     * Outbox 생성 정적 팩토리 메서드
     */
    public static Outbox create(String aggregateType, String aggregateId, 
                                String eventType, String payload) {
        return Outbox.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .retryCount(0)
                .build();
    }
}
