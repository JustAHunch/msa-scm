package com.logistics.scm.oms.inventory.event.order;

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
 * Order Service에서 발행하는 이벤트
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private String eventId;
    private String orderId;
    private String orderNumber;
    private String customerId;
    private List<OrderItemEvent> items;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemEvent {
        private String productCode;
        private Integer quantity;
        private BigDecimal price;
        private String warehouseId;
    }
}
