package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * OutboundOrder Entity - 출고 주문
 * 
 * 출고 타입:
 * - OB (Sales Outbound): 일반 출고 (우리 창고 → 외부 고객)
 * - TO (Transfer Outbound): 허브 간 이동 출고 (A 창고 → B 창고로 이동 시작)
 * - RTN_OB (Return Outbound): 반품 출고 (TODO)
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
@Table(name = "OUTBOUND_ORDER_TB", indexes = {
    @Index(name = "idx_outbound_number", columnList = "outbound_number", unique = true),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_outbound_status", columnList = "outbound_status"),
    @Index(name = "idx_outbound_type", columnList = "outbound_type")
})
public class OutboundOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "outbound_id", columnDefinition = "uuid")
    private UUID outboundId;

    @Column(name = "outbound_number", nullable = false, unique = true, length = 50)
    private String outboundNumber;

    @Column(name = "warehouse_id", nullable = false, columnDefinition = "uuid")
    private UUID warehouseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "outbound_type", nullable = false, length = 20)
    private OutboundType outboundType;

    @Enumerated(EnumType.STRING)
    @Column(name = "outbound_status", nullable = false, length = 20)
    private OutboundStatus outboundStatus;

    @Column(name = "order_number", length = 50)
    private String orderNumber;

    @Column(name = "destination_warehouse_id", columnDefinition = "uuid")
    private UUID destinationWarehouseId;

    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @OneToMany(mappedBy = "outboundOrder", cascade = CascadeType.ALL)
    private List<OutboundItem> outboundItems = new ArrayList<>();

    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * 출고 상태
     */
    public enum OutboundStatus {
        SCHEDULED,   // 예정
        PICKING,     // 피킹 중
        PACKING,     // 패킹 중
        SHIPPED,     // 출고 완료
        CANCELLED    // 취소
    }

    /**
     * 출고 타입
     * OB: Sales Outbound (일반 출고 - 우리 창고 → 외부 고객)
     * TO: Transfer Outbound (허브 간 이동 출고 - A 창고 → B 창고로 이동)
     * RTN_OB: Return Outbound (반품 출고 - TODO)
     */
    public enum OutboundType {
        OB,      // Sales Outbound (일반 출고)
        TO,      // Transfer Outbound (허브 간 이동 출고)
        RTN_OB   // Return Outbound (반품 출고) - TODO
    }

    // Getter aliases
    public UUID getId() {
        return this.outboundId;
    }

    // Business Methods
    /**
     * 출고 시작
     */
    public void startPicking() {
        if (this.outboundStatus != OutboundStatus.SCHEDULED) {
            throw new IllegalStateException("예정 상태에서만 피킹을 시작할 수 있습니다.");
        }
        this.outboundStatus = OutboundStatus.PICKING;
    }

    /**
     * 패킹 시작
     */
    public void startPacking() {
        if (this.outboundStatus != OutboundStatus.PICKING) {
            throw new IllegalStateException("피킹 완료 후 패킹을 시작할 수 있습니다.");
        }
        this.outboundStatus = OutboundStatus.PACKING;
    }

    /**
     * 출고 완료
     */
    public void complete() {
        if (this.outboundStatus != OutboundStatus.PACKING && 
            this.outboundStatus != OutboundStatus.PICKING) {
            throw new IllegalStateException("피킹 또는 패킹 상태에서만 출고를 완료할 수 있습니다.");
        }
        this.outboundStatus = OutboundStatus.SHIPPED;
        this.shippedDate = LocalDateTime.now();
    }

    /**
     * 출고 취소
     */
    public void cancel(String reason) {
        if (this.outboundStatus == OutboundStatus.SHIPPED) {
            throw new IllegalStateException("이미 출고 완료된 주문은 취소할 수 없습니다.");
        }
        this.outboundStatus = OutboundStatus.CANCELLED;
        this.remarks = reason;
    }
}
