package com.logistics.scm.oms.inventory.domain.inbound.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 입고 신청 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboundCreateRequest {

    @NotNull(message = "창고 ID는 필수입니다")
    private UUID warehouseId;

    @NotBlank(message = "상품 코드는 필수입니다")
    private String productCode;

    @NotBlank(message = "상품명은 필수입니다")
    private String productName;

    @NotNull(message = "요청 수량은 필수입니다")
    @Positive(message = "요청 수량은 양수여야 합니다")
    private Integer requestedQty;

    @NotNull(message = "업체 ID는 필수입니다")
    private UUID companyId;

    private String remarks;
}
