package com.logistics.scm.oms.order.event.outbox.service;

import com.logistics.scm.oms.order.event.outbox.entity.Outbox;
import com.logistics.scm.oms.order.event.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Outbox Service 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    public Outbox saveOutbox(String aggregateType, String aggregateId, 
                            String eventType, String payload) {
        log.debug("Outbox 이벤트 저장: aggregateType={}, aggregateId={}, eventType={}", 
                aggregateType, aggregateId, eventType);

        Outbox outbox = Outbox.create(aggregateType, aggregateId, eventType, payload);
        return outboxRepository.save(outbox);
    }

    @Override
    @Transactional
    public void markAsPublished(Outbox outbox) {
        log.debug("Outbox 발행 성공 처리: outboxId={}", outbox.getId());
        outbox.markAsPublished();
        outboxRepository.save(outbox);
    }

    @Override
    @Transactional
    public void markAsFailed(Outbox outbox, String errorMessage) {
        log.warn("Outbox 발행 실패 처리: outboxId={}, error={}", outbox.getId(), errorMessage);
        outbox.markAsFailed(errorMessage);
        outboxRepository.save(outbox);
    }
}
