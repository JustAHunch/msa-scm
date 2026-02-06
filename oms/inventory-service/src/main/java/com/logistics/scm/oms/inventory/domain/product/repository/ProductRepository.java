package com.logistics.scm.oms.inventory.domain.product.repository;

import com.logistics.scm.oms.inventory.domain.product.entity.Product;
import com.logistics.scm.oms.inventory.domain.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 상품 Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * 상품 코드로 조회
     */
    Optional<Product> findByProductCode(String productCode);

    /**
     * 상품 코드 존재 여부
     */
    boolean existsByProductCode(String productCode);

    /**
     * 업체 ID로 상품 목록 조회
     */
    List<Product> findByCompanyId(UUID companyId);

    /**
     * 상태별 상품 목록 조회
     */
    List<Product> findByStatus(ProductStatus status);

    /**
     * 업체 ID + 상태별 상품 조회
     */
    List<Product> findByCompanyIdAndStatus(UUID companyId, ProductStatus status);

    /**
     * 카테고리별 상품 조회
     */
    List<Product> findByCategory(String category);

    /**
     * 상품명 검색 (LIKE)
     */
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
}
