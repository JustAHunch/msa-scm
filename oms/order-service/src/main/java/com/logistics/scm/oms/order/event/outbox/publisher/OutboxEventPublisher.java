package com.logistics.scm.oms.order.event.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.scm.oms.order.event.order.OrderCancelledEvent;
import com.logistics.scm.oms.order.event.order.OrderCreatedEvent;
import com.logistics.scm.oms.order.event.outbox.entity.Outbox;
import com.logistics.scm.oms.order.event.outbox.entity.OutboxStatus;
import com.logistics.scm.oms.order.event.outbox.repository.OutboxRepository;
import com.logistics.scm.oms.order.event.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Outbox Event Publisher
 * 
 * 주기적으로 Outbox 테이블을 폴링하여 미발행 이벤트를 Kafka로 발행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxRepository outboxRepository;
    private final OutboxService outboxService;
    private final KafkaTemplate<String, OrderCreatedEvent> orderCreatedEventKafkaTemplate;
    private final KafkaTemplate<String, OrderCancelledEvent> orderCancelledEventKafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.order-events}")
    private String orderEventsTopic;

    /**
     * 발행 대기 중인 이벤트 처리
     * 매 5초마다 실행
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    public void publishPendingEvents() {
        log.debug("발행 대기 중인 Outbox 이벤트 폴링 시작");

        List<Outbox> pendingOutboxes = outboxRepository.findByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING);

        if (pendingOutboxes.isEmpty()) {
            log.debug("발행 대기 중인 Outbox 이벤트 없음");
            return;
        }

        log.info("발행 대기 중인 Outbox 이벤트: {}개", pendingOutboxes.size());

        for (Outbox outbox : pendingOutboxes) {
            try {
                publishEvent(outbox);
            } catch (Exception e) {
                log.error("Outbox 이벤트 발행 실패: outboxId={}", outbox.getId(), e);
                outboxService.markAsFailed(outbox, e.getMessage());
            }
        }
    }

    /**
     * 발행 실패한 이벤트 재시도
     * 매 30초마다 실행
     */
    @Scheduled(fixedDelay = 30000, initialDelay = 30000)
    public void retryFailedEvents() {
        log.debug("발행 실패한 Outbox 이벤트 재시도 시작");

        // 마지막 시도 후 1분 경과한 것만 재시도
        LocalDateTime retryThreshold = LocalDateTime.now().minusMinutes(1);
        List<Outbox> failedOutboxes = outboxRepository
                .findByStatusAndLastAttemptAtBeforeOrderByCreatedAtAsc(
                        OutboxStatus.FAILED, retryThreshold);

        if (failedOutboxes.isEmpty()) {
            log.debug("재시도 대상 Outbox 이벤트 없음");
            return;
        }

        log.info("재시도 대상 Outbox 이벤트: {}개", failedOutboxes.size());

        for (Outbox outbox : failedOutboxes) {
            if (!outbox.canRetry()) {
                log.warn("최대 재시도 횟수 초과: outboxId={}, retryCount={}", 
                        outbox.getId(), outbox.getRetryCount());
                continue;
            }

            try {
                publishEvent(outbox);
            } catch (Exception e) {
                log.error("Outbox 이벤트 재시도 실패: outboxId={}", outbox.getId(), e);
                outboxService.markAsFailed(outbox, e.getMessage());
            }
        }
    }

    /**
     * 발행 완료된 오래된 이벤트 정리
     * 매일 새벽 2시에 실행
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupPublishedEvents() {
        log.info("발행 완료된 Outbox 이벤트 정리 시작");

        // 7일 이상 지난 발행 완료 이벤트 삭제
        LocalDateTime cleanupThreshold = LocalDateTime.now().minusDays(7);
        List<Outbox> oldPublishedOutboxes = outboxRepository
                .findByStatusAndPublishedAtBefore(OutboxStatus.PUBLISHED, cleanupThreshold);

        if (!oldPublishedOutboxes.isEmpty()) {
            outboxRepository.deleteAll(oldPublishedOutboxes);
            log.info("발행 완료된 오래된 Outbox 이벤트 정리 완료: {}개", oldPublishedOutboxes.size());
        } else {
            log.info("정리할 Outbox 이벤트 없음");
        }
    }

    /**
     * 이벤트 타입에 따라 Kafka로 발행
     */
    private void publishEvent(Outbox outbox) throws Exception {
        log.debug("Outbox 이벤트 발행 시작: outboxId={}, eventType={}", 
                outbox.getId(), outbox.getEventType());

        switch (outbox.getEventType()) {
            case "OrderCreatedEvent" -> publishOrderCreatedEvent(outbox);
            case "OrderCancelledEvent" -> publishOrderCancelledEvent(outbox);
            default -> throw new IllegalArgumentException(
                    "Unknown event type: " + outbox.getEventType());
        }
    }

    /**
     * OrderCreatedEvent 발행
     */
    private void publishOrderCreatedEvent(Outbox outbox) throws Exception {
        OrderCreatedEvent event = objectMapper.readValue(
                outbox.getPayload(), OrderCreatedEvent.class);

        orderCreatedEventKafkaTemplate.send(orderEventsTopic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        outboxService.markAsPublished(outbox);
                        log.info("OrderCreatedEvent 발행 성공: outboxId={}, orderId={}", 
                                outbox.getId(), event.getOrderId());
                    } else {
                        outboxService.markAsFailed(outbox, ex.getMessage());
                        log.error("OrderCreatedEvent 발행 실패: outboxId={}", outbox.getId(), ex);
                    }
                });
    }

    /**
     * OrderCancelledEvent 발행
     */
    private void publishOrderCancelledEvent(Outbox outbox) throws Exception {
        OrderCancelledEvent event = objectMapper.readValue(
                outbox.getPayload(), OrderCancelledEvent.class);

        orderCancelledEventKafkaTemplate.send(orderEventsTopic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        outboxService.markAsPublished(outbox);
                        log.info("OrderCancelledEvent 발행 성공: outboxId={}, orderId={}", 
                                outbox.getId(), event.getOrderId());
                    } else {
                        outboxService.markAsFailed(outbox, ex.getMessage());
                        log.error("OrderCancelledEvent 발행 실패: outboxId={}", outbox.getId(), ex);
                    }
                });
    }
}
