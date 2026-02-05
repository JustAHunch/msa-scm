package com.logistics.scm.oms.order.event.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 재고 예약 완료 이벤트
 * 
 * Inventory Service에서 발행하는 이벤트
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservedEvent {

    private String eventId;
    private String orderId;
    private String orderNumber;
    private List<ReservationItem> reservations;
    private LocalDateTime reservedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReservationItem {
        private String productCode;
        private Integer quantity;
        private String warehouseId;
    }
}
