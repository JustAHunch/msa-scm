package com.logistics.scm.oms.inventory.domain.inventory.exception;

import com.logistics.scm.oms.inventory.common.exception.BusinessException;
import com.logistics.scm.oms.inventory.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class InsufficientStockException extends BusinessException {
    private final String productCode;
    private final Integer requestedQuantity;
    private final Integer availableQuantity;

    public InsufficientStockException(String productCode, Integer requestedQuantity, Integer availableQuantity) {
        super(ErrorCode.INSUFFICIENT_STOCK);
        this.productCode = productCode;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String getMessage() {
        return String.format("%s (상품코드: %s, 요청수량: %d, 가용재고: %d)",
                super.getMessage(), productCode, requestedQuantity, availableQuantity);
    }
}