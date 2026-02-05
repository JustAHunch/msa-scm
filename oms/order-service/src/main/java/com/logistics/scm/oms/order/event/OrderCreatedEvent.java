package com.logistics.scm.oms.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 생성 이벤트
 * 
 * 주문이 생성되었을 때 Kafka로 발행되는 이벤트
 * Inventory Service가 이 이벤트를 구독하여 재고를 차감함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

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
     * 고객 ID
     */
    private String customerId;

    /**
     * 주문 항목 리스트
     */
    private List<OrderItemEvent> items;

    /**
     * 총 금액
     */
    private BigDecimal totalAmount;

    /**
     * 이벤트 발생 시각
     */
    private LocalDateTime createdAt;

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
         * 단가
         */
        private BigDecimal price;

        /**
         * 창고 ID (재고 차감할 창고)
         */
        private String warehouseId;
    }
}
