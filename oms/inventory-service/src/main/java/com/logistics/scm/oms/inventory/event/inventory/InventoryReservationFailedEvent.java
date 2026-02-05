package com.logistics.scm.oms.inventory.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 재고 예약 실패 이벤트
 * 
 * 재고가 부족하여 예약이 실패했을 때 Kafka로 발행되는 이벤트
 * Order Service가 이 이벤트를 구독하여 주문을 취소함 (보상 트랜잭션)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservationFailedEvent {

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
     * 실패 사유
     */
    private String reason;

    /**
     * 상품 코드 (재고 부족 상품)
     */
    private String productCode;

    /**
     * 요청 수량
     */
    private Integer requestedQuantity;

    /**
     * 가용 수량
     */
    private Integer availableQuantity;

    /**
     * 이벤트 발생 시각
     */
    private LocalDateTime failedAt;
}
