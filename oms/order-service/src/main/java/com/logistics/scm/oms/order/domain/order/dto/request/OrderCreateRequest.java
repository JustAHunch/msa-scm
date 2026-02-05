package com.logistics.scm.oms.order.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 주문 생성 요청 DTO
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Schema(description = "주문 생성 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequestDTO {

    @Schema(description = "고객 ID", required = true, example = "c1234567-1234-1234-1234-123456789012")
    @NotNull(message = "고객 ID는 필수입니다")
    private UUID customerId;

    @Schema(description = "주문 항목 목록", required = true)
    @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
    @Valid
    private List<OrderItemRequestDTO> items;

    /**
     * 주문 항목 요청 DTO
     */
    @Schema(description = "주문 항목 정보")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequestDTO {

        @Schema(description = "상품 코드", required = true, example = "PROD-001")
        @NotNull(message = "상품 코드는 필수입니다")
        private String productCode;

        @Schema(description = "수량", required = true, example = "5")
        @NotNull(message = "수량은 필수입니다")
        private Integer quantity;

        @Schema(description = "단가", required = true, example = "30000.00")
        @NotNull(message = "단가는 필수입니다")
        private BigDecimal unitPrice;
    }
}
