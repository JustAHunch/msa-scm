package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * OutboundItem Entity - 출고 항목
 * 
 * @author c.h.jo
 * @since 2026-02-06
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "OUTBOUND_ITEM_TB", indexes = {
    @Index(name = "idx_outbound_id", columnList = "outbound_id"),
    @Index(name = "idx_product_code", columnList = "product_code")
})
public class OutboundItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "outbound_item_id", columnDefinition = "uuid")
    private UUID outboundItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_id", nullable = false)
    private OutboundOrder outboundOrder;

    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @Column(name = "ordered_quantity", nullable = false)
    private Integer orderedQuantity;

    @Column(name = "picked_quantity", nullable = false)
    private Integer pickedQuantity;

    @Column(name = "location_code", length = 50)
    private String locationCode;

    @Column(name = "remarks", length = 200)
    private String remarks;

    // Getter aliases
    public UUID getId() {
        return this.outboundItemId;
    }

    // Business Methods
    /**
     * 피킹 수량 업데이트
     */
    public void updatePickedQuantity(Integer quantity) {
        if (quantity > this.orderedQuantity) {
            throw new IllegalArgumentException("피킹 수량은 주문 수량을 초과할 수 없습니다.");
        }
        this.pickedQuantity = quantity;
    }

    /**
     * 피킹 완료 여부
     */
    public boolean isPickingComplete() {
        return this.pickedQuantity.equals(this.orderedQuantity);
    }
}
