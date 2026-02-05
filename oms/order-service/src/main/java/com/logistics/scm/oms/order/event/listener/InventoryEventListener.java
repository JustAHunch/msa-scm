package com.logistics.scm.oms.order.event.listener;

import com.logistics.scm.oms.order.event.inventory.InventoryReleasedEvent;
import com.logistics.scm.oms.order.event.inventory.InventoryReservationFailedEvent;
import com.logistics.scm.oms.order.event.inventory.InventoryReservedEvent;
import com.logistics.scm.oms.order.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 재고 이벤트 리스너
 * 
 * Inventory Service에서 발행한 이벤트를 구독하여 주문 상태 관리 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final OrderService orderService;

    /**
     * 재고 예약 성공 이벤트 처리
     * 주문 상태를 CONFIRMED로 업데이트
     */
    @KafkaListener(
            topics = "${kafka.topics.inventory-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "inventoryReservedEventKafkaListenerContainerFactory"
    )
    public void handleInventoryReservedEvent(InventoryReservedEvent event) {
        log.info("재고 예약 성공 이벤트 수신: orderId={}, orderNumber={}", 
                event.getOrderId(), event.getOrderNumber());

        try {
            UUID orderId = UUID.fromString(event.getOrderId());
            
            // 주문 상태를 CONFIRMED로 업데이트
            orderService.confirmOrder(orderId);
            
            log.info("주문 확정 완료: orderId={}, orderNumber={}", 
                    event.getOrderId(), event.getOrderNumber());

        } catch (Exception e) {
            log.error("재고 예약 성공 이벤트 처리 중 오류 발생: orderId={}", 
                    event.getOrderId(), e);
        }
    }

    /**
     * 재고 예약 실패 이벤트 처리
     * 주문을 취소 (보상 트랜잭션)
     */
    @KafkaListener(
            topics = "${kafka.topics.inventory-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "inventoryReservationFailedEventKafkaListenerContainerFactory"
    )
    public void handleInventoryReservationFailedEvent(InventoryReservationFailedEvent event) {
        log.warn("재고 예약 실패 이벤트 수신: orderId={}, orderNumber={}, reason={}", 
                event.getOrderId(), event.getOrderNumber(), event.getReason());

        try {
            UUID orderId = UUID.fromString(event.getOrderId());
            
            // 주문 취소 (보상 트랜잭션)
            orderService.cancelOrderByInventoryFailure(orderId, event.getReason());
            
            log.info("재고 부족으로 주문 취소 완료: orderId={}, orderNumber={}", 
                    event.getOrderId(), event.getOrderNumber());

        } catch (Exception e) {
            log.error("재고 예약 실패 이벤트 처리 중 오류 발생: orderId={}", 
                    event.getOrderId(), e);
        }
    }

    /**
     * 재고 해제 완료 이벤트 처리
     * 로깅 및 모니터링 목적
     */
    @KafkaListener(
            topics = "${kafka.topics.inventory-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "inventoryReleasedEventKafkaListenerContainerFactory"
    )
    public void handleInventoryReleasedEvent(InventoryReleasedEvent event) {
        log.info("재고 해제 완료 이벤트 수신: orderId={}, orderNumber={}", 
                event.getOrderId(), event.getOrderNumber());
        
        // 현재는 로깅만 수행
        // 향후 통계/분석 목적으로 활용 가능
    }
}
