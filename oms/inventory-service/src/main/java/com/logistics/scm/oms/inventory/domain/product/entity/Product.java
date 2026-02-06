package com.logistics.scm.oms.inventory.domain.product.entity;

import com.logistics.scm.oms.inventory.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Product Entity
 *
 * 상품 정보를 관리합니다.
 * - 상품명, 규격, 보관 조건 등
 * - 업체별 상품 등록
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
@Table(name = "PRODUCT_TB",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_code", columnNames = {"product_code"})
    },
    indexes = {
        @Index(name = "idx_product_company_id", columnList = "company_id"),
        @Index(name = "idx_product_category", columnList = "category"),
        @Index(name = "idx_product_status", columnList = "status")
    }
)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", columnDefinition = "uuid")
    private UUID productId;

    /**
     * 상품 코드 (시스템 고유)
     */
    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    private String productCode;

    /**
     * 상품명
     */
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /**
     * 상품 설명
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 카테고리
     */
    @Column(name = "category", length = 100)
    private String category;

    /**
     * 규격 (크기/사양)
     */
    @Column(name = "specification", length = 500)
    private String specification;

    /**
     * 단위 (EA, BOX, KG 등)
     */
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    /**
     * 무게 (kg)
     */
    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;

    /**
     * 가로 (cm)
     */
    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;

    /**
     * 세로 (cm)
     */
    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;

    /**
     * 높이 (cm)
     */
    @Column(name = "depth", precision = 10, scale = 2)
    private BigDecimal depth;

    /**
     * 보관 조건 (상온, 냉장, 냉동 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_condition", nullable = false, length = 20)
    private StorageCondition storageCondition;

    /**
     * 업체 ID (상품 등록 업체)
     */
    @Column(name = "company_id", nullable = false, columnDefinition = "uuid")
    private UUID companyId;

    /**
     * 상품 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProductStatus status;

    // Getter alias
    public UUID getId() {
        return this.productId;
    }

    // Business Methods

    /**
     * 상품 활성화
     */
    public void activate() {
        this.status = ProductStatus.ACTIVE;
    }

    /**
     * 상품 비활성화
     */
    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    /**
     * 상품 단종
     */
    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    /**
     * 상품 정보 수정
     */
    public void update(String productName, String description, String category,
                       String specification, String unit, BigDecimal weight,
                       StorageCondition storageCondition) {
        if (productName != null) this.productName = productName;
        if (description != null) this.description = description;
        if (category != null) this.category = category;
        if (specification != null) this.specification = specification;
        if (unit != null) this.unit = unit;
        if (weight != null) this.weight = weight;
        if (storageCondition != null) this.storageCondition = storageCondition;
    }
}
