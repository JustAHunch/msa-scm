package com.logistics.scm.oms.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 재고 예약 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveStockRequestDTO {

    /**
     * 창고 ID
     */
    @NotNull(message = "창고 ID는 필수입니다")
    private UUID warehouseId;

    /**
     * 상품 코드
     */
    @NotBlank(message = "상품 코드는 필수입니다")
    private String productCode;

    /**
     * 예약 수량
     */
    @NotNull(message = "수량은 필수입니다")
    @Positive(message = "수량은 양수여야 합니다")
    private Integer quantity;

    /**
     * 참조 주문 ID (주문 번호 등)
     */
    @NotBlank(message = "참조 주문 ID는 필수입니다")
    private String referenceOrderId;

    /**
     * 비고
     */
    private String remarks;
}
