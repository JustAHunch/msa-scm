package com.logistics.scm.oms.inventory.domain.inbound.dto.response;

import com.logistics.scm.oms.inventory.domain.inbound.entity.Inbound;
import com.logistics.scm.oms.inventory.domain.inbound.entity.InboundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 입고 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboundResponse {

    private UUID id;
    private String inboundNumber;
    private UUID warehouseId;
    private String productCode;
    private String productName;
    private Integer requestedQty;
    private Integer actualQty;
    private Integer rejectedQty;
    private UUID companyId;
    private InboundStatus status;
    private String inspectorId;
    private LocalDateTime inspectedAt;
    private String rejectionReason;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity에서 DTO로 변환
     */
    public static InboundResponse from(Inbound inbound) {
        return InboundResponse.builder()
                .id(inbound.getId())
                .inboundNumber(inbound.getInboundNumber())
                .warehouseId(inbound.getWarehouseId())
                .productCode(inbound.getProductCode())
                .productName(inbound.getProductName())
                .requestedQty(inbound.getRequestedQty())
                .actualQty(inbound.getActualQty())
                .rejectedQty(inbound.getRejectedQty())
                .companyId(inbound.getCompanyId())
                .status(inbound.getStatus())
                .inspectorId(inbound.getInspectorId())
                .inspectedAt(inbound.getInspectedAt())
                .rejectionReason(inbound.getRejectionReason())
                .remarks(inbound.getRemarks())
                .createdAt(inbound.getCreatedAt())
                .updatedAt(inbound.getUpdatedAt())
                .build();
    }
}
