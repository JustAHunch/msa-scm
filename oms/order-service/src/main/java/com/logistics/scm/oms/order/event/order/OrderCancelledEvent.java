package com.logistics.scm.oms.order.event.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 취소 이벤트
 * 
 * 주문이 취소되었을 때 Kafka로 발행되는 이벤트
 * Inventory Service가 이 이벤트를 구독하여 예약된 재고를 해제함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelledEvent {

    /**
     * 이벤트 고유 ID
     */
    private String eventId;

    /**
     * 주문 ID
     */
    private String orderId;

    /**
     * 주문 번호
     */
    private String orderNumber;

    /**
     * 취소 사유
     */
    private String cancelReason;

    /**
     * 주문 항목 리스트 (재고 원복용)
     */
    private List<OrderItemEvent> items;

    /**
     * 이벤트 발생 시각
     */
    private LocalDateTime cancelledAt;

    /**
     * 주문 항목 이벤트
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemEvent {
        
        /**
         * 상품 코드
         */
        private String productCode;

        /**
         * 수량
         */
        private Integer quantity;

        /**
         * 창고 ID
         */
        private String warehouseId;
    }
}
