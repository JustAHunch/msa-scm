package com.logistics.scm.oms.inventory.event.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 취소 이벤트
 * 
 * Order Service에서 발행하는 이벤트
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelledEvent {

    private String eventId;
    private String orderId;
    private String orderNumber;
    private String cancelReason;
    private List<OrderItemEvent> items;
    private LocalDateTime cancelledAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemEvent {
        private String productCode;
        private Integer quantity;
        private String warehouseId;
    }
}
