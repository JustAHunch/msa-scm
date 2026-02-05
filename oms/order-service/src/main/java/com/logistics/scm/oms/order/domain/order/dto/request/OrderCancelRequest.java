package com.logistics.scm.oms.order.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 취소 요청 DTO
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Schema(description = "주문 취소 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelRequest {

    @Schema(description = "취소 사유", example = "고객 변심", required = true)
    @NotBlank(message = "취소 사유는 필수입니다")
    private String cancelReason;
}
