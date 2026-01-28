package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ReturnOrder Entity - 반송 주문
 * 
 * 입고 거부, 배송 실패 등으로 인한 반송을 관리합니다.
 * 
 * 주요 기능:
 * - 반송 사유 및 상태 관리
 * - 귀책사유(공급자/구매자/배송업체/시스템) 추적
 * - 반송 비용 부담 주체 결정 근거 제공
 * - 반송 이력 관리
 * 
 * 비즈니스 규칙:
 * - 공급자 귀책: 품질 불량, 수량 불일치, 오배송, 파손
 * - 구매자 귀책: 고객 취소, 단순 변심
 * - 배송업체 귀책: 배송 실패(배송기사 과실)
 * - 시스템 귀책: 시스템 오류로 인한 반송
 * 
 * @author c.h.jo
 * @since 2025-01-28
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "RETURN_ORDER_TB", indexes = {
    @Index(name = "idx_return_number", columnList = "return_number", unique = true),
    @Index(name = "idx_original_inbound_id", columnList = "original_inbound_id"),
    @Index(name = "idx_original_order_id", columnList = "original_order_id"),
    @Index(name = "idx_return_status", columnList = "return_status"),
    @Index(name = "idx_liability_party", columnList = "liability_party")
})
public class ReturnOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "return_id", columnDefinition = "uuid")
    private UUID returnId;

    /**
     * 반송 번호 (시스템 자동 생성)
     * Format: RTN-YYYYMMDD-XXXXX
     */
    @Column(name = "return_number", nullable = false, unique = true, length = 50)
    private String returnNumber;

    /**
     * 원본 입고 주문 ID
     * 입고 거부로 인한 반송인 경우 참조
     */
    @Column(name = "original_inbound_id", columnDefinition = "uuid")
    private UUID originalInboundId;

    /**
     * 원본 배송 주문 ID
     * 배송 실패로 인한 반송인 경우 참조
     */
    @Column(name = "original_order_id", columnDefinition = "uuid")
    private UUID originalOrderId;

    /**
     * 반송 사유
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "return_reason", nullable = false, length = 50)
    private ReturnReason returnReason;

    /**
     * 반송 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "return_status", nullable = false, length = 20)
    private ReturnStatus returnStatus;

    /**
     * 귀책 주체
     * 비용 부담 결정의 근거가 됨
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "liability_party", nullable = false, length = 20)
    private LiabilityParty liabilityParty;

    /**
     * 반송 요청 일시
     */
    @Column(name = "return_requested_at", nullable = false)
    private LocalDateTime returnRequestedAt;

    /**
     * 반송 완료 일시
     */
    @Column(name = "return_completed_at")
    private LocalDateTime returnCompletedAt;

    /**
     * 반송 상세 사유 (추가 설명)
     */
    @Column(name = "return_note", length = 1000)
    private String returnNote;

    /**
     * 반송 출발지 주소
     */
    @Column(name = "return_from_address", length = 500)
    private String returnFromAddress;

    /**
     * 반송 목적지 주소
     */
    @Column(name = "return_to_address", length = 500)
    private String returnToAddress;

    /**
     * 반송 사유 Enum
     */
    public enum ReturnReason {
        /**
         * 품질 불량 (공급자 귀책)
         * 검수 시 품질 기준 미달
         */
        QUALITY_FAIL("품질 불량"),

        /**
         * 수량 불일치 (공급자 귀책)
         * 예정 수량과 실제 수량 불일치
         */
        QUANTITY_MISMATCH("수량 불일치"),

        /**
         * 오배송 (공급자 귀책)
         * 주문한 상품과 다른 상품 배송
         */
        WRONG_PRODUCT("오배송"),

        /**
         * 파손 (공급자 또는 배송업체 귀책)
         * 배송 중 상품 파손
         */
        DAMAGE("파손"),

        /**
         * 고객 취소 (구매자 귀책)
         * 고객의 주문 취소 요청
         */
        CUSTOMER_CANCEL("고객 취소"),

        /**
         * 배송 실패 (구매자 또는 배송업체 귀책)
         * 수령 거부, 주소 오류, 배송기사 과실 등
         */
        DELIVERY_FAIL("배송 실패"),

        /**
         * 기타 사유
         */
        OTHER("기타");

        private final String description;

        ReturnReason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 반송 상태 Enum
     */
    public enum ReturnStatus {
        /**
         * 반송 요청 (초기 상태)
         */
        REQUESTED("반송 요청"),

        /**
         * 반송 승인
         */
        APPROVED("반송 승인"),

        /**
         * 반송 중
         */
        IN_TRANSIT("반송 중"),

        /**
         * 반송 완료
         */
        COMPLETED("반송 완료"),

        /**
         * 반송 취소
         */
        CANCELLED("반송 취소");

        private final String description;

        ReturnStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 귀책 주체 Enum
     * 반송 비용 부담 주체 결정의 근거
     */
    public enum LiabilityParty {
        /**
         * 공급자 귀책
         * 부담 비용: 왕복 운송비 전액
         * 사유: 품질 불량, 수량 불일치, 오배송, 파손
         */
        SUPPLIER("공급자"),

        /**
         * 구매자 귀책
         * 부담 비용: 왕복 운송비 전액
         * 사유: 고객 취소, 단순 변심, 수령 거부
         */
        CUSTOMER("구매자"),

        /**
         * 배송업체 귀책
         * 부담 비용: 배송업체가 비용 부담
         * 사유: 배송 실패(배송기사 과실), 차량 고장
         */
        CARRIER("배송업체"),

        /**
         * 시스템 귀책
         * 부담 비용: 시스템 운영사가 비용 부담
         * 사유: 시스템 오류로 인한 반송
         */
        SYSTEM("시스템");

        private final String description;

        LiabilityParty(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ========================================
    // Business Methods
    // ========================================

    /**
     * 반송 승인 처리
     */
    public void approve() {
        if (this.returnStatus != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("반송 요청 상태에서만 승인할 수 있습니다.");
        }
        this.returnStatus = ReturnStatus.APPROVED;
    }

    /**
     * 반송 시작 처리
     */
    public void startReturn() {
        if (this.returnStatus != ReturnStatus.APPROVED) {
            throw new IllegalStateException("반송 승인 상태에서만 시작할 수 있습니다.");
        }
        this.returnStatus = ReturnStatus.IN_TRANSIT;
    }

    /**
     * 반송 완료 처리
     */
    public void complete() {
        if (this.returnStatus != ReturnStatus.IN_TRANSIT) {
            throw new IllegalStateException("반송 중 상태에서만 완료할 수 있습니다.");
        }
        this.returnStatus = ReturnStatus.COMPLETED;
        this.returnCompletedAt = LocalDateTime.now();
    }

    /**
     * 반송 취소 처리
     */
    public void cancel() {
        if (this.returnStatus == ReturnStatus.COMPLETED) {
            throw new IllegalStateException("완료된 반송은 취소할 수 없습니다.");
        }
        this.returnStatus = ReturnStatus.CANCELLED;
    }

    /**
     * 공급자 귀책 여부 확인
     * 반송 비용 정산 시 사용
     */
    public boolean isSupplierLiability() {
        return this.liabilityParty == LiabilityParty.SUPPLIER;
    }

    /**
     * 구매자 귀책 여부 확인
     * 반송 비용 정산 시 사용
     */
    public boolean isCustomerLiability() {
        return this.liabilityParty == LiabilityParty.CUSTOMER;
    }

    /**
     * 배송업체 귀책 여부 확인
     * 반송 비용 정산 시 사용
     */
    public boolean isCarrierLiability() {
        return this.liabilityParty == LiabilityParty.CARRIER;
    }
}
