package com.logistics.scm.oms.inventory.domain.product.exception;

import com.logistics.scm.oms.inventory.common.exception.EntityNotFoundException;
import com.logistics.scm.oms.inventory.common.exception.ErrorCode;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ProductNotFoundException extends EntityNotFoundException {
    private final String identifier;

    public ProductNotFoundException(UUID productId) {
        super(ErrorCode.PRODUCT_NOT_FOUND);
        this.identifier = productId.toString();
    }

    public ProductNotFoundException(String productCode) {
        super(ErrorCode.PRODUCT_NOT_FOUND);
        this.identifier = productCode;
    }

    @Override
    public String getMessage() {
        return String.format("%s (식별자: %s)", super.getMessage(), identifier);
    }
}
