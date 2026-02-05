package com.logistics.scm.oms.inventory.domain.inventory.dto.response;

import com.logistics.scm.oms.inventory.domain.inventory.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재고 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    /**
     * 재고 ID
     */
    private UUID id;

    /**
     * 창고 ID
     */
    private UUID warehouseId;

    /**
     * 상품 코드
     */
    private String productCode;

    /**
     * 가용 수량
     */
    private Integer availableQuantity;

    /**
     * 할당 수량
     */
    private Integer allocatedQuantity;

    /**
     * 총 수량
     */
    private Integer totalQuantity;

    /**
     * 안전 재고
     */
    private Integer safetyStock;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * Entity에서 DTO로 변환
     *
     * @param inventory 재고 Entity
     * @return 재고 응답 DTO
     */
    public static InventoryResponse from(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .warehouseId(inventory.getWarehouseId())
                .productCode(inventory.getProductCode())
                .availableQuantity(inventory.getAvailableQuantity())
                .allocatedQuantity(inventory.getAllocatedQuantity())
                .totalQuantity(inventory.getTotalQuantity())
                .safetyStock(inventory.getSafetyStock())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
