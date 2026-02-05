package com.logistics.scm.oms.inventory.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.scm.oms.inventory.domain.inventory.dto.request.ReleaseStockRequest;
import com.logistics.scm.oms.inventory.domain.inventory.dto.request.ReserveStockRequest;
import com.logistics.scm.oms.inventory.event.inventory.InventoryReleasedEvent;
import com.logistics.scm.oms.inventory.event.inventory.InventoryReservationFailedEvent;
import com.logistics.scm.oms.inventory.event.inventory.InventoryReservedEvent;
import com.logistics.scm.oms.inventory.event.order.OrderCancelledEvent;
import com.logistics.scm.oms.inventory.event.order.OrderCreatedEvent;
import com.logistics.scm.oms.inventory.domain.inventory.exception.InsufficientStockException;
import com.logistics.scm.oms.inventory.domain.inventory.service.InventoryService;
import com.logistics.scm.oms.inventory.event.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 주문 이벤트 리스너
 * 
 * Order Service에서 발행한 이벤트를 구독하여 재고 관리 수행
 * Outbox Pattern 적용: 재고 변경과 이벤트를 동일 트랜잭션으로 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final InventoryService inventoryService;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    /**
     * 주문 생성 이벤트 처리
     * 재고를 예약하고 결과 이벤트를 Outbox에 저장
     */
    @KafkaListener(
            topics = "${kafka.topics.order-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderCreatedEventKafkaListenerContainerFactory"
    )
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("주문 생성 이벤트 수신: orderId={}, orderNumber={}", 
                event.getOrderId(), event.getOrderNumber());

        try {
            // 주문 항목별로 재고 예약
            List<InventoryReservedEvent.ReservationItem> reservations = new ArrayList<>();

            for (OrderCreatedEvent.OrderItemEvent item : event.getItems()) {
                ReserveStockRequest request = ReserveStockRequest.builder()
                        .warehouseId(UUID.fromString(item.getWarehouseId()))
                        .productCode(item.getProductCode())
                        .quantity(item.getQuantity())
                        .referenceOrderId(event.getOrderNumber())
                        .remarks("주문 생성으로 인한 재고 예약")
                        .build();

                // 재고 예약
                inventoryService.reserveStock(request);

                // 예약 성공 항목 추가
                reservations.add(InventoryReservedEvent.ReservationItem.builder()
                        .productCode(item.getProductCode())
                        .quantity(item.getQuantity())
                        .warehouseId(item.getWarehouseId())
                        .build());

                log.info("재고 예약 성공: productCode={}, quantity={}", 
                        item.getProductCode(), item.getQuantity());
            }

            // 재고 예약 성공 이벤트를 Outbox에 저장
            InventoryReservedEvent reservedEvent = InventoryReservedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .orderNumber(event.getOrderNumber())
                    .reservations(reservations)
                    .reservedAt(LocalDateTime.now())
                    .build();

            String payload = objectMapper.writeValueAsString(reservedEvent);
            outboxService.saveOutbox(
                    "Inventory",
                    event.getOrderId(),
                    "InventoryReservedEvent",
                    payload
            );
            log.info("재고 예약 성공 이벤트 Outbox 저장 완료: orderId={}", event.getOrderId());

        } catch (InsufficientStockException e) {
            log.warn("재고 부족으로 주문 실패: orderId={}, productCode={}, requestedQty={}, availableQty={}",
                    event.getOrderId(), e.getProductCode(), e.getRequestedQuantity(), e.getAvailableQuantity());

            // 재고 예약 실패 이벤트를 Outbox에 저장
            InventoryReservationFailedEvent failedEvent = InventoryReservationFailedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .orderNumber(event.getOrderNumber())
                    .reason(e.getMessage())
                    .productCode(e.getProductCode())
                    .requestedQuantity(e.getRequestedQuantity())
                    .availableQuantity(e.getAvailableQuantity())
                    .failedAt(LocalDateTime.now())
                    .build();

            try {
                String payload = objectMapper.writeValueAsString(failedEvent);
                outboxService.saveOutbox(
                        "Inventory",
                        event.getOrderId(),
                        "InventoryReservationFailedEvent",
                        payload
                );
                log.info("재고 예약 실패 이벤트 Outbox 저장 완료: orderId={}", event.getOrderId());
            } catch (Exception ex) {
                log.error("재고 예약 실패 이벤트 Outbox 저장 실패: orderId={}", event.getOrderId(), ex);
            }

        } catch (Exception e) {
            log.error("주문 생성 이벤트 처리 중 오류 발생: orderId={}", event.getOrderId(), e);

            // 일반 오류도 실패 이벤트로 Outbox에 저장
            InventoryReservationFailedEvent failedEvent = InventoryReservationFailedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .orderNumber(event.getOrderNumber())
                    .reason("재고 처리 중 시스템 오류: " + e.getMessage())
                    .failedAt(LocalDateTime.now())
                    .build();

            try {
                String payload = objectMapper.writeValueAsString(failedEvent);
                outboxService.saveOutbox(
                        "Inventory",
                        event.getOrderId(),
                        "InventoryReservationFailedEvent",
                        payload
                );
            } catch (Exception ex) {
                log.error("재고 예약 실패 이벤트 Outbox 저장 실패: orderId={}", event.getOrderId(), ex);
            }
        }
    }

    /**
     * 주문 취소 이벤트 처리
     * 예약된 재고를 해제하고 결과 이벤트를 Outbox에 저장
     */
    @KafkaListener(
            topics = "${kafka.topics.order-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderCancelledEventKafkaListenerContainerFactory"
    )
    public void handleOrderCancelledEvent(OrderCancelledEvent event) {
        log.info("주문 취소 이벤트 수신: orderId={}, orderNumber={}", 
                event.getOrderId(), event.getOrderNumber());

        try {
            // 주문 항목별로 재고 해제
            List<InventoryReleasedEvent.ReleaseItem> releases = new ArrayList<>();

            for (OrderCancelledEvent.OrderItemEvent item : event.getItems()) {
                ReleaseStockRequest request = ReleaseStockRequest.builder()
                        .warehouseId(UUID.fromString(item.getWarehouseId()))
                        .productCode(item.getProductCode())
                        .quantity(item.getQuantity())
                        .referenceOrderId(event.getOrderNumber())
                        .remarks("주문 취소로 인한 재고 원복: " + event.getCancelReason())
                        .build();

                // 재고 해제
                inventoryService.releaseStock(request);

                // 해제 성공 항목 추가
                releases.add(InventoryReleasedEvent.ReleaseItem.builder()
                        .productCode(item.getProductCode())
                        .quantity(item.getQuantity())
                        .warehouseId(item.getWarehouseId())
                        .build());

                log.info("재고 해제 성공: productCode={}, quantity={}", 
                        item.getProductCode(), item.getQuantity());
            }

            // 재고 해제 완료 이벤트를 Outbox에 저장
            InventoryReleasedEvent releasedEvent = InventoryReleasedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .orderNumber(event.getOrderNumber())
                    .releases(releases)
                    .releasedAt(LocalDateTime.now())
                    .build();

            String payload = objectMapper.writeValueAsString(releasedEvent);
            outboxService.saveOutbox(
                    "Inventory",
                    event.getOrderId(),
                    "InventoryReleasedEvent",
                    payload
            );
            log.info("재고 해제 완료 이벤트 Outbox 저장 완료: orderId={}", event.getOrderId());

        } catch (Exception e) {
            log.error("주문 취소 이벤트 처리 중 오류 발생: orderId={}", event.getOrderId(), e);
        }
    }
}
