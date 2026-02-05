package com.logistics.scm.oms.inventory.repository;

import com.logistics.scm.oms.inventory.entity.StockMovement;
import com.logistics.scm.oms.inventory.entity.StockMovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 재고 이동 이력 Repository
 *
 * 재고 변동 이력을 조회하는 데이터 접근 계층
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID>, StockMovementRepositoryCustom {

    /**
     * 재고ID로 이동 이력 조회
     *
     * @param inventoryId 재고 ID
     * @return 이동 이력 목록
     */
    List<StockMovement> findByInventoryId(UUID inventoryId);

    /**
     * 재고ID와 이동 유형으로 이력 조회
     *
     * @param inventoryId 재고 ID
     * @param movementType 이동 유형
     * @return 이동 이력 목록
     */
    List<StockMovement> findByInventoryIdAndMovementType(UUID inventoryId, StockMovementType movementType);

    /**
     * 참조 주문ID로 이동 이력 조회
     *
     * @param referenceOrderId 참조 주문 ID
     * @return 이동 이력 목록
     */
    List<StockMovement> findByReferenceOrderId(String referenceOrderId);
}
