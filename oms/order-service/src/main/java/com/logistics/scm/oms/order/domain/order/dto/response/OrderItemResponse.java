package com.logistics.scm.oms.order.domain.order.dto.response;

import com.logistics.scm.oms.order.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 주문 아이템 응답 DTO
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
@Builder
@Schema(description = "주문 항목 정보")
public class OrderItemResponseDTO {

    @Schema(description = "주문 항목 ID", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID id;

    @Schema(description = "상품 코드", example = "PROD-001")
    private String productCode;

    @Schema(description = "수량", example = "5")
    private Integer quantity;

    @Schema(description = "단가", example = "30000.00")
    private BigDecimal unitPrice;

    @Schema(description = "소계 (수량 × 단가)", example = "150000.00")
    private BigDecimal subtotal;

    /**
     * Entity를 DTO로 변환
     */
    public static OrderItemResponseDTO from(OrderItem item) {
        return OrderItemResponseDTO.builder()
                .id(item.getId())
                .productCode(item.getProductCode())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
