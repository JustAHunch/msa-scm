package com.logistics.scm.oms.inventory.domain.inbound.entity;

import com.logistics.scm.oms.inventory.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Inbound Entity
 *
 * 허브 입고 신청 및 검수 프로세스를 관리합니다.
 * - 입고 신청 → 검수 → 합격(재고 등록) / 불합격(반송)
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
@Table(name = "INBOUND_TB",
    indexes = {
        @Index(name = "idx_inbound_warehouse_id", columnList = "warehouse_id"),
        @Index(name = "idx_inbound_product_code", columnList = "product_code"),
        @Index(name = "idx_inbound_company_id", columnList = "company_id"),
        @Index(name = "idx_inbound_status", columnList = "status"),
        @Index(name = "idx_inbound_number", columnList = "inbound_number")
    }
)
public class Inbound extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inbound_id", columnDefinition = "uuid")
    private UUID inboundId;

    /**
     * 입고 번호
     */
    @Column(name = "inbound_number", nullable = false, unique = true, length = 50)
    private String inboundNumber;

    /**
     * 입고 허브(창고) ID
     */
    @Column(name = "warehouse_id", nullable = false, columnDefinition = "uuid")
    private UUID warehouseId;

    /**
     * 상품 코드
     */
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    /**
     * 상품명
     */
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /**
     * 입고 요청 수량
     */
    @Column(name = "requested_qty", nullable = false)
    private Integer requestedQty;

    /**
     * 실제 입고 수량 (검수 후 확정)
     */
    @Column(name = "actual_qty")
    private Integer actualQty;

    /**
     * 불량 수량
     */
    @Column(name = "rejected_qty")
    private Integer rejectedQty;

    /**
     * 업체 ID (입고 요청 업체)
     */
    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID companyId;

    /**
     * 입고 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InboundStatus status;

    /**
     * 검수자 ID
     */
    @Column(name = "inspector_id", length = 100)
    private String inspectorId;

    /**
     * 검수 완료 시간
     */
    @Column(name = "inspected_at")
    private LocalDateTime inspectedAt;

    /**
     * 반송 사유 (불합격 시)
     */
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    /**
     * 비고
     */
    @Column(name = "remarks", length = 500)
    private String remarks;

    // Getter alias
    public UUID getId() {
        return this.inboundId;
    }

    // Business Methods

    /**
     * 검수 시작
     */
    public void startInspection(String inspectorId) {
        if (this.status != InboundStatus.REQUESTED) {
            throw new IllegalStateException("입고 신청 상태에서만 검수를 시작할 수 있습니다.");
        }
        this.status = InboundStatus.INSPECTING;
        this.inspectorId = inspectorId;
    }

    /**
     * 검수 합격 (전량)
     */
    public void approve(Integer actualQty) {
        if (this.status != InboundStatus.INSPECTING) {
            throw new IllegalStateException("검수 중 상태에서만 합격 처리할 수 있습니다.");
        }
        this.status = InboundStatus.APPROVED;
        this.actualQty = actualQty;
        this.rejectedQty = this.requestedQty - actualQty;
        this.inspectedAt = LocalDateTime.now();
    }

    /**
     * 검수 불합격 (반송)
     */
    public void reject(String rejectionReason) {
        if (this.status != InboundStatus.INSPECTING) {
            throw new IllegalStateException("검수 중 상태에서만 불합격 처리할 수 있습니다.");
        }
        this.status = InboundStatus.REJECTED;
        this.actualQty = 0;
        this.rejectedQty = this.requestedQty;
        this.rejectionReason = rejectionReason;
        this.inspectedAt = LocalDateTime.now();
    }

    /**
     * 입고 완료 (재고 반영 완료)
     */
    public void complete() {
        if (this.status != InboundStatus.APPROVED) {
            throw new IllegalStateException("합격 상태에서만 입고 완료 처리할 수 있습니다.");
        }
        this.status = InboundStatus.COMPLETED;
    }
}
