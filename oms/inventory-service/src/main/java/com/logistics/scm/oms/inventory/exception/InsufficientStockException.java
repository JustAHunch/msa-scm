package com.logistics.scm.oms.inventory.exception;

/**
 * 재고 부족 예외
 *
 * 요청한 수량보다 가용 재고가 적을 때 발생
 */
public class InsufficientStockException extends RuntimeException {

    private final String productCode;
    private final Integer requestedQuantity;
    private final Integer availableQuantity;

    public InsufficientStockException(String productCode, Integer requestedQuantity, Integer availableQuantity) {
        super(String.format("재고가 부족합니다. 상품코드: %s, 요청수량: %d, 가용재고: %d",
                productCode, requestedQuantity, availableQuantity));
        this.productCode = productCode;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
}
