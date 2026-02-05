package com.logistics.scm.oms.inventory.event.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 재고 해제 완료 이벤트
 * 
 * 주문 취소로 인해 재고가 원복되었을 때 Kafka로 발행되는 이벤트
 * Order Service가 이 이벤트를 구독하여 로깅/모니터링 목적으로 사용
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReleasedEvent {

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
     * 해제된 재고 항목 리스트
     */
    private List<ReleaseItem> releases;

    /**
     * 이벤트 발생 시각
     */
    private LocalDateTime releasedAt;

    /**
     * 해제 항목
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReleaseItem {
        
        /**
         * 상품 코드
         */
        private String productCode;

        /**
         * 해제 수량
         */
        private Integer quantity;

        /**
         * 창고 ID
         */
        private String warehouseId;
    }
}
