package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * InboundItem Entity - 입고 항목
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "INBOUND_ITEM_TB", indexes = {
    @Index(name = "idx_inbound_id", columnList = "inbound_id"),
    @Index(name = "idx_product_code", columnList = "product_code"),
    @Index(name = "idx_qc_status", columnList = "qc_status")
})
public class InboundItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inbound_item_id", columnDefinition = "uuid")
    private UUID inboundItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inbound_item_inbound"))
    private InboundOrder inboundOrder;

    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @Column(name = "expected_qty", nullable = false)
    private Integer expectedQty;

    @Column(name = "received_qty")
    private Integer receivedQty;

    @Column(name = "location_id", columnDefinition = "uuid")
    private UUID locationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "qc_status", length = 20)
    private QcStatus qcStatus;

    public enum QcStatus {
        PENDING, PASS, FAIL
    }
}
