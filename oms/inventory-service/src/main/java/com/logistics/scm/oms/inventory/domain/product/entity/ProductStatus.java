package com.logistics.scm.oms.inventory.domain.product.entity;

/**
 * 상품 상태
 */
public enum ProductStatus {
    ACTIVE,         // 활성 (판매 가능)
    INACTIVE,       // 비활성 (일시 중단)
    DISCONTINUED    // 단종
}
