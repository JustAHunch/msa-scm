package com.scm.warehouse.domain.inbound.dto.request;

import com.scm.warehouse.entity.InboundOrder.InboundType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 입고 생성 요청 DTO
 * 
 * @author c.h.jo
 * @since 2026-02-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundCreateRequest {

    @NotNull(message = "창고 ID는 필수입니다.")
    private UUID warehouseId;

    @NotNull(message = "입고 타입은 필수입니다.")
    private InboundType inboundType;

    private String supplierName;

    private UUID sourceWarehouseId; // TO에서 사용 (출발 창고 ID)

    private LocalDate expectedDate;

    @NotEmpty(message = "입고 항목은 최소 1개 이상이어야 합니다.")
    private List<InboundItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InboundItemRequest {
        @NotNull(message = "상품 코드는 필수입니다.")
        private String productCode;

        @NotNull(message = "예정 수량은 필수입니다.")
        private Integer expectedQty;

        private UUID locationId;
    }
}
