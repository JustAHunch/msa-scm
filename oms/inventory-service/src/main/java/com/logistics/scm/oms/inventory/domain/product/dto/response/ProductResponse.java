package com.logistics.scm.oms.inventory.domain.product.dto.response;

import com.logistics.scm.oms.inventory.domain.product.entity.Product;
import com.logistics.scm.oms.inventory.domain.product.entity.ProductStatus;
import com.logistics.scm.oms.inventory.domain.product.entity.StorageCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 상품 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private UUID id;
    private String productCode;
    private String productName;
    private String description;
    private String category;
    private String specification;
    private String unit;
    private BigDecimal weight;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;
    private StorageCondition storageCondition;
    private UUID companyId;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity에서 DTO로 변환
     */
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .description(product.getDescription())
                .category(product.getCategory())
                .specification(product.getSpecification())
                .unit(product.getUnit())
                .weight(product.getWeight())
                .width(product.getWidth())
                .height(product.getHeight())
                .depth(product.getDepth())
                .storageCondition(product.getStorageCondition())
                .companyId(product.getCompanyId())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
