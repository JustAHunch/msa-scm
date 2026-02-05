package com.logistics.scm.oms.inventory.entity;

import com.logistics.scm.oms.inventory.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * StockMovement Entity
 * 
 * 재고 이동 내역을 관리합니다.
 * - 입고, 출고, 조정, 예약, 해제 등 모든 재고 변동 이력
 * - 변경 전후 수량 추적
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "STOCK_MOVEMENT_TB", indexes = {
    @Index(name = "idx_inventory_id", columnList = "inventory_id"),
    @Index(name = "idx_movement_type", columnList = "movement_type"),
    @Index(name = "idx_reference_type", columnList = "reference_type"),
    @Index(name = "idx_reference_id", columnList = "reference_id"),
    @Index(name = "idx_movement_date", columnList = "movement_date")
})
public class StockMovement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "movement_id", columnDefinition = "uuid")
    private UUID movementId;

    @Column(name = "inventory_id", nullable = false, columnDefinition = "uuid")
    private UUID inventoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private MovementType movementType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reference_type", nullable = false, length = 50)
    private String referenceType;

    @Column(name = "reference_id", nullable = false, columnDefinition = "uuid")
    private UUID referenceId;

    @Column(name = "previous_qty", nullable = false)
    private Integer previousQty;

    @Column(name = "current_qty", nullable = false)
    private Integer currentQty;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    // Enum for Movement Type
    public enum MovementType {
        IN,         // 입고
        OUT,        // 출고
        ADJUST,     // 재고 조정
        RESERVE,    // 예약
        RELEASE     // 예약 해제
    }

}
