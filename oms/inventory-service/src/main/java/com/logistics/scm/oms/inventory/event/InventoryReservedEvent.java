package com.logistics.scm.oms.inventory.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 재고 예약 완료 이벤트
 * 
 * 재고 예약이 성공했을 때 Kafka로 발행되는 이벤트
 * Order Service가 이 이벤트를 구독하여 주문 상태를 CONFIRMED로 업데이트함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservedEvent {

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
     * 예약된 재고 항목 리스트
     */
    private List<ReservationItem> reservations;

    /**
     * 이벤트 발생 시각
     */
    private LocalDateTime reservedAt;

    /**
     * 예약 항목
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReservationItem {
        
        /**
         * 상품 코드
         */
        private String productCode;

        /**
         * 예약 수량
         */
        private Integer quantity;

        /**
         * 창고 ID
         */
        private String warehouseId;
    }
}
