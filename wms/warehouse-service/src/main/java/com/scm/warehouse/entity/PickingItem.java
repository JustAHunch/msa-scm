package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * PickingItem Entity - 피킹 항목
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "PICKING_ITEM_TB", indexes = {
    @Index(name = "idx_picking_list_id", columnList = "picking_list_id"),
    @Index(name = "idx_product_code", columnList = "product_code"),
    @Index(name = "idx_location_id", columnList = "location_id")
})
public class PickingItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "picking_item_id", columnDefinition = "uuid")
    private UUID pickingItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picking_list_id", nullable = false, foreignKey = @ForeignKey(name = "fk_picking_item_list"))
    private PickingList pickingList;

    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @Column(name = "location_id", nullable = false, columnDefinition = "uuid")
    private UUID locationId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "picked_qty")
    private Integer pickedQty;

}
