package com.logistics.scm.oms.inventory.domain.inventory.entity;

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

    @Column(name = "in_transit_qty", nullable = false)
    private Integer inTransitQty;

    @Column(name = "hold_qty", nullable = false)
    private Integer holdQty;

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

    public Integer getInTransitQuantity() {
        return this.inTransitQty;
    }

    public Integer getHoldQuantity() {
        return this.holdQty;
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

    /**
     * 허브 간 이동 출고 (Transfer Out)
     * 출발 허브의 가용 재고를 차감하고 이동 중 재고로 전환
     */
    public void transferOut(Integer quantity) {
        if (this.availableQty < quantity) {
            throw new IllegalStateException("가용 재고가 부족합니다.");
        }
        this.availableQty -= quantity;
        this.totalQty -= quantity;
        // 출발 허브에서는 이동 중 재고를 추적하지 않음 (목적지 허브에서 추적)
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 허브 간 이동 입고 (Transfer In)
     * 목적지 허브의 재고를 증가 (이동 중 재고 → 가용 재고)
     */
    public void transferIn(Integer quantity) {
        this.totalQty += quantity;
        this.availableQty += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 이동 중 재고 증가
     * TO 완료 시 이동 중 상태로 기록 (전체 시스템 재고 추적용)
     */
    public void increaseInTransit(Integer quantity) {
        this.inTransitQty += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 이동 중 재고 감소
     * TI 완료 시 이동 중 재고를 가용 재고로 전환
     */
    public void decreaseInTransit(Integer quantity) {
        if (this.inTransitQty < quantity) {
            throw new IllegalStateException("이동 중 재고가 부족합니다.");
        }
        this.inTransitQty -= quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 보류/불량 재고 처리
     * 입고 검수 불합격 또는 파손 상품을 보류 상태로 전환
     */
    public void hold(Integer quantity) {
        if (this.availableQty < quantity) {
            throw new IllegalStateException("가용 재고가 부족합니다.");
        }
        this.availableQty -= quantity;
        this.holdQty += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 보류 재고 해제
     * 보류 상태에서 다시 판매 가능 상태로 전환
     */
    public void releaseHold(Integer quantity) {
        if (this.holdQty < quantity) {
            throw new IllegalStateException("보류 재고가 부족합니다.");
        }
        this.holdQty -= quantity;
        this.availableQty += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * 보류 재고 폐기
     * 파손/불량으로 폐기 처리
     */
    public void discardHold(Integer quantity) {
        if (this.holdQty < quantity) {
            throw new IllegalStateException("보류 재고가 부족합니다.");
        }
        this.holdQty -= quantity;
        this.totalQty -= quantity;
        this.lastUpdated = LocalDateTime.now();
    }

}
