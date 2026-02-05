package com.logistics.scm.oms.inventory.exception;

import java.util.UUID;

/**
 * 재고 없음 예외
 *
 * 요청한 재고 정보가 존재하지 않을 때 발생
 */
public class InventoryNotFoundException extends RuntimeException {

    private final UUID warehouseId;
    private final String productCode;

    public InventoryNotFoundException(UUID warehouseId, String productCode) {
        super(String.format("재고를 찾을 수 없습니다. 창고ID: %s, 상품코드: %s",
                warehouseId, productCode));
        this.warehouseId = warehouseId;
        this.productCode = productCode;
    }

    public InventoryNotFoundException(UUID inventoryId) {
        super(String.format("재고를 찾을 수 없습니다. 재고ID: %s", inventoryId));
        this.warehouseId = null;
        this.productCode = null;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public String getProductCode() {
        return productCode;
    }
}
