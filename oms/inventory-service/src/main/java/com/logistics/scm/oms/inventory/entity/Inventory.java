package com.logistics.scm.oms.inventory.entity;

import com.logistics.scm.oms.inventory.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Inventory Entity
 * 
 * 재고 정보를 관리합니다.
 * - 멀티 창고 재고 관리
 * - 가용 수량, 할당 수량, 총 수량 관리
 * - 안전 재고 관리
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "INVENTORY_TB", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_warehouse_product", columnNames = {"warehouse_id", "product_code"})
    },
    indexes = {
        @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
        @Index(name = "idx_product_code", columnList = "product_code"),
        @Index(name = "idx_last_updated", columnList = "last_updated")
    }
)
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inventory_id", columnDefinition = "uuid")
    private UUID inventoryId;

    @Column(name = "warehouse_id", nullable = false, columnDefinition = "uuid")
    private UUID warehouseId;

    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @Column(name = "available_qty", nullable = false)
    private Integer availableQty;

    @Column(name = "allocated_qty", nullable = false)
    private Integer allocatedQty;

    @Column(name = "total_qty", nullable = false)
    private Integer totalQty;

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    // Getter aliases for better readability
    public UUID getId() {
        return this.inventoryId;
    }

    public Integer getAvailableQuantity() {
        return this.availableQty;
    }

    public Integer getAllocatedQuantity() {
        return this.allocatedQty;
    }

    public Integer getTotalQuantity() {
        return this.totalQty;
    }

    // Business Methods
    /**
     * 재고 예약 (주문 시) - reserve로 메서드명 변경
     * 가용수량을 감소시키고 할당수량을 증가시킴
     */
    public void reserve(Integer quantity) {
        if (this.availableQty < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.availableQty -= quantity;
        this.allocatedQty += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 재고 예약 해제 (주문 취소 시) - release로 메서드명 변경
     * 할당수량을 감소시키고 가용수량을 증가시킴
     */
    public void release(Integer quantity) {
        if (this.allocatedQty < quantity) {
            throw new IllegalStateException("할당된 재고가 부족합니다.");
        }
        this.allocatedQty -= quantity;
        this.availableQty += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 재고 차감 (출고 시)
     * 할당된 재고를 실제로 차감
     */
    public void deduct(Integer quantity) {
        if (this.allocatedQty < quantity) {
            throw new IllegalStateException("할당된 재고가 부족합니다.");
        }
        this.allocatedQty -= quantity;
        this.totalQty -= quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 재고 추가 (입고 시)
     * 총 수량과 가용 수량을 증가시킴
     */
    public void add(Integer quantity) {
        this.totalQty += quantity;
        this.availableQty += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 가용 수량 증가 (재고 보충)
     */
    public void increaseAvailableQuantity(Integer quantity) {
        this.availableQty += quantity;
        this.totalQty += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 안전 재고 업데이트
     */
    public void updateSafetyStock(Integer safetyStock) {
        this.safetyStock = safetyStock;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 안전 재고 미달 여부 확인
     */
    public boolean isBelowSafetyStock() {
        return this.availableQty < this.safetyStock;
    }

}
