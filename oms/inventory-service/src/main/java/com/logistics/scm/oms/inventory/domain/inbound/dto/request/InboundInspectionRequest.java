package com.logistics.scm.oms.inventory.domain.inbound.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 입고 검수 결과 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboundInspectionRequest {

    /**
     * 합격 여부
     */
    @NotNull(message = "합격 여부는 필수입니다")
    private Boolean approved;

    /**
     * 실제 입고 수량 (합격 시 필수)
     */
    @PositiveOrZero(message = "실제 입고 수량은 0 이상이어야 합니다")
    private Integer actualQty;

    /**
     * 검수자 ID
     */
    @NotBlank(message = "검수자 ID는 필수입니다")
    private String inspectorId;

    /**
     * 불합격 사유 (불합격 시 필수)
     */
    private String rejectionReason;

    /**
     * 안전 재고 (합격 시 재고 등록 안전재고 설정)
     */
    @PositiveOrZero(message = "안전 재고는 0 이상이어야 합니다")
    private Integer safetyStock;
}
