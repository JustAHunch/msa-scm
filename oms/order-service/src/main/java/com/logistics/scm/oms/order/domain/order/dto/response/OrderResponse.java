package com.logistics.scm.oms.order.domain.order.dto.response;

import com.logistics.scm.oms.order.domain.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 주문 정보 응답 DTO
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
@Builder
@Schema(description = "주문 정보 응답")
public class OrderResponseDTO {

    @Schema(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "주문 번호", example = "ORD-20260205-0001")
    private String orderNumber;

    @Schema(description = "고객 ID", example = "c1234567-1234-1234-1234-123456789012")
    private UUID customerId;

    @Schema(description = "주문 상태", example = "CREATED")
    private String status;

    @Schema(description = "총 금액", example = "150000.00")
    private BigDecimal totalAmount;

    @Schema(description = "주문 항목 목록")
    private List<OrderItemResponseDTO> items;

    @Schema(description = "생성 일시", example = "2026-02-05T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2026-02-05T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "생성자", example = "SYSTEM")
    private String createdBy;

    @Schema(description = "수정자", example = "SYSTEM")
    private String updatedBy;

    /**
     * Entity를 DTO로 변환
     */
    public static OrderResponseDTO from(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems().stream()
                        .map(OrderItemResponseDTO::from)
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .createdBy(order.getCreatedBy())
                .updatedBy(order.getUpdatedBy())
                .build();
    }
}
