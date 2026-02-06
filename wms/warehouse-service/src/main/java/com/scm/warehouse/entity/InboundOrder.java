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
 * InboundOrder Entity - 입고 주문
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
@Table(name = "INBOUND_ORDER_TB", indexes = {
    @Index(name = "idx_inbound_number", columnList = "inbound_number", unique = true),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_inbound_status", columnList = "inbound_status")
})
public class InboundOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inbound_id", columnDefinition = "uuid")
    private UUID inboundId;

    @Column(name = "inbound_number", nullable = false, unique = true, length = 50)
    private String inboundNumber;

    @Column(name = "warehouse_id", nullable = false, columnDefinition = "uuid")
    private UUID warehouseId;

    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    @Enumerated(EnumType.STRING)
    @Column(name = "inbound_status", nullable = false, length = 20)
    private InboundStatus inboundStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "inbound_type", nullable = false, length = 20)
    private InboundType inboundType;

    @Column(name = "expected_date")
    private LocalDate expectedDate;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @OneToMany(mappedBy = "inboundOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InboundItem> inboundItems = new ArrayList<>();

    /**
     * 입고 상태
     */
    public enum InboundStatus {
        SCHEDULED,   // 예정
        RECEIVING,   // 입고 중
        COMPLETED,   // 완료
        REJECTED     // 거부 (품질 불량)
    }

    /**
     * 입고 타입
     * IB: Purchase Inbound (일반 입고 - 외부 업체 → 우리 창고)
     * TI: Transfer Inbound (허브 간 이동 입고 - A 창고 → B 창고)
     * RTN_IB: Return Inbound (반품 입고 - TODO)
     */
    public enum InboundType {
        IB,      // Purchase Inbound (일반 입고)
        TI,      // Transfer Inbound (간선입고)
        RTN_IB   // Return Inbound (반품 입고) - TODO
    }

    // Getter aliases
    public UUID getId() {
        return this.inboundId;
    }

    // Business Methods
    /**
     * 입고 항목 추가
     */
    public void addInboundItem(InboundItem item) {
        this.inboundItems.add(item);
        item.setInboundOrder(this);
    }

    /**
     * 입고 승인
     */
    public void approve() {
        if (this.inboundStatus != InboundStatus.SCHEDULED) {
            throw new IllegalStateException("예정 상태에서만 승인할 수 있습니다.");
        }
        this.inboundStatus = InboundStatus.RECEIVING;
    }

    /**
     * 입고 완료
     */
    public void complete() {
        if (this.inboundStatus != InboundStatus.RECEIVING) {
            throw new IllegalStateException("입고 중 상태에서만 완료할 수 있습니다.");
        }
        this.inboundStatus = InboundStatus.COMPLETED;
        this.receivedDate = LocalDateTime.now();
    }

    /**
     * 입고 거부
     */
    public void reject() {
        if (this.inboundStatus == InboundStatus.COMPLETED) {
            throw new IllegalStateException("완료된 입고는 거부할 수 없습니다.");
        }
        this.inboundStatus = InboundStatus.REJECTED;
    }
}

