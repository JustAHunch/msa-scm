package com.logistics.scm.oms.inventory.domain.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 재고 생성/업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInventoryRequest {

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
     * 수량
     */
    @NotNull(message = "수량은 필수입니다")
    @Positive(message = "수량은 양수여야 합니다")
    private Integer quantity;

    /**
     * 안전 재고 (선택사항)
     */
    @PositiveOrZero(message = "안전 재고는 0 이상이어야 합니다")
    private Integer safetyStock;
}
