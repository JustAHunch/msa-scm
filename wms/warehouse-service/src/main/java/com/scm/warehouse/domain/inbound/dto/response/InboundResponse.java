package com.scm.warehouse.domain.inbound.dto.response;

import com.scm.warehouse.entity.InboundItem;
import com.scm.warehouse.entity.InboundOrder;
import com.scm.warehouse.entity.InboundOrder.InboundStatus;
import com.scm.warehouse.entity.InboundOrder.InboundType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 입고 응답 DTO
 * 
 * @author c.h.jo
 * @since 2026-02-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundResponse {

    private UUID inboundId;
    private String inboundNumber;
    private UUID warehouseId;
    private InboundType inboundType;
    private InboundStatus inboundStatus;
    private String supplierName;
    private LocalDate expectedDate;
    private LocalDateTime receivedDate;
    private List<InboundItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity → DTO 변환
     */
    public static InboundResponse from(InboundOrder inboundOrder) {
        return InboundResponse.builder()
                .inboundId(inboundOrder.getInboundId())
                .inboundNumber(inboundOrder.getInboundNumber())
                .warehouseId(inboundOrder.getWarehouseId())
                .inboundType(inboundOrder.getInboundType())
                .inboundStatus(inboundOrder.getInboundStatus())
                .supplierName(inboundOrder.getSupplierName())
                .expectedDate(inboundOrder.getExpectedDate())
                .receivedDate(inboundOrder.getReceivedDate())
                .items(inboundOrder.getInboundItems().stream()
                        .map(InboundItemResponse::from)
                        .collect(Collectors.toList()))
                .createdAt(inboundOrder.getCreatedAt())
                .updatedAt(inboundOrder.getUpdatedAt())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InboundItemResponse {
        private UUID inboundItemId;
        private String productCode;
        private Integer expectedQty;
        private Integer receivedQty;
        private String qcStatus;
        private UUID locationId;

        public static InboundItemResponse from(InboundItem item) {
            return InboundItemResponse.builder()
                    .inboundItemId(item.getInboundItemId())
                    .productCode(item.getProductCode())
                    .expectedQty(item.getExpectedQty())
                    .receivedQty(item.getReceivedQty())
                    .qcStatus(item.getQcStatus() != null ? item.getQcStatus().name() : null)
                    .locationId(item.getLocationId())
                    .build();
        }
    }
}
