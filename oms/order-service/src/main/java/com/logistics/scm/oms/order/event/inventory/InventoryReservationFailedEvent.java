package com.logistics.scm.oms.order.event.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 재고 예약 실패 이벤트
 * 
 * Inventory Service에서 발행하는 이벤트
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservationFailedEvent {

    private String eventId;
    private String orderId;
    private String orderNumber;
    private String reason;
    private String productCode;
    private Integer requestedQuantity;
    private Integer availableQuantity;
    private LocalDateTime failedAt;
}
