package com.logistics.scm.oms.inventory.domain.product.exception;

import com.logistics.scm.oms.inventory.common.exception.DuplicateEntityException;
import com.logistics.scm.oms.inventory.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateProductCodeException extends DuplicateEntityException {
    private final String productCode;

    public DuplicateProductCodeException(String productCode) {
        super(ErrorCode.DUPLICATE_ENTITY);
        this.productCode = productCode;
    }

    @Override
    public String getMessage() {
        return String.format("이미 존재하는 상품 코드입니다. (상품코드: %s)", productCode);
    }
}
