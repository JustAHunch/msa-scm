package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "expected_date")
    private LocalDate expectedDate;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @OneToMany(mappedBy = "inboundOrder", cascade = CascadeType.ALL)
    private List<InboundItem> inboundItems = new ArrayList<>();

    public enum InboundStatus {
        SCHEDULED, RECEIVING, COMPLETED
    }

}
