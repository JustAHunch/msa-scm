package com.logistics.scm.oms.inventory.repository;

import com.logistics.scm.oms.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 재고 Repository
 *
 * 재고 조회, 수정, 삭제 등의 데이터 접근 계층
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID>, InventoryRepositoryCustom {

    /**
     * 창고ID와 상품코드로 재고 조회
     *
     * @param warehouseId 창고 ID
     * @param productCode 상품 코드
     * @return 재고 정보
     */
    Optional<Inventory> findByWarehouseIdAndProductCode(UUID warehouseId, String productCode);

    /**
     * 상품코드로 모든 창고의 재고 조회
     *
     * @param productCode 상품 코드
     * @return 재고 목록
     */
    List<Inventory> findByProductCode(String productCode);

    /**
     * 창고ID로 모든 재고 조회
     *
     * @param warehouseId 창고 ID
     * @return 재고 목록
     */
    List<Inventory> findByWarehouseId(UUID warehouseId);

    /**
     * 창고ID와 상품코드로 재고 존재 여부 확인
     *
     * @param warehouseId 창고 ID
     * @param productCode 상품 코드
     * @return 존재 여부
     */
    boolean existsByWarehouseIdAndProductCode(UUID warehouseId, String productCode);
}
