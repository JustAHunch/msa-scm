package com.logistics.scm.oms.inventory.domain.product.dto.request;

import com.logistics.scm.oms.inventory.domain.product.entity.StorageCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 상품 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequest {

    @NotBlank(message = "상품 코드는 필수입니다")
    private String productCode;

    @NotBlank(message = "상품명은 필수입니다")
    private String productName;

    private String description;

    private String category;

    private String specification;

    @NotBlank(message = "단위는 필수입니다")
    private String unit;

    private BigDecimal weight;

    private BigDecimal width;

    private BigDecimal height;

    private BigDecimal depth;

    @NotNull(message = "보관 조건은 필수입니다")
    private StorageCondition storageCondition;

    @NotNull(message = "업체 ID는 필수입니다")
    private UUID companyId;
}
