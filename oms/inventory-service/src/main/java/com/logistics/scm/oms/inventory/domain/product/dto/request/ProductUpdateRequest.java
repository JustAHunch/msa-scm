package com.logistics.scm.oms.inventory.domain.product.dto.request;

import com.logistics.scm.oms.inventory.domain.product.entity.StorageCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 상품 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {

    private String productName;

    private String description;

    private String category;

    private String specification;

    private String unit;

    private BigDecimal weight;

    private StorageCondition storageCondition;
}
