package com.logistics.scm.oms.inventory.exception;

import com.logistics.scm.oms.inventory.common.exception.EntityNotFoundException;
import com.logistics.scm.oms.inventory.common.exception.ErrorCode;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InventoryNotFoundException extends EntityNotFoundException {
    private final UUID warehouseId;
    private final String productCode;
    private final UUID inventoryId;

    public InventoryNotFoundException(UUID warehouseId, String productCode) {
        super(ErrorCode.INVENTORY_NOT_FOUND);
        this.warehouseId = warehouseId;
        this.productCode = productCode;
        this.inventoryId = null;
    }

    public InventoryNotFoundException(UUID inventoryId) {
        super(ErrorCode.INVENTORY_NOT_FOUND);
        this.warehouseId = null;
        this.productCode = null;
        this.inventoryId = inventoryId;
    }

    @Override
    public String getMessage() {
        if (inventoryId != null) {
            return String.format("%s (재고ID: %s)", super.getMessage(), inventoryId);
        }
        return String.format("%s (창고ID: %s, 상품코드: %s)", 
                super.getMessage(), warehouseId, productCode);
    }
}