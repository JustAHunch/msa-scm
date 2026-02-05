package com.logistics.scm.oms.inventory.domain.inventory.respository;

import com.logistics.scm.oms.inventory.domain.inventory.entity.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 재고 Repository Custom 인터페이스
 * 
 * QueryDSL을 사용하는 복잡한 쿼리 메서드 정의
 */
public interface InventoryRepositoryCustom {

    /**
     * 재고가 부족한 상품 조회 (가용수량 < 안전재고)
     *
     * @return 재고 부족 상품 목록
     */
    List<Inventory> findLowStockInventories();

    /**
     * 창고ID와 상품코드로 재고 조회 (비관적 락)
     * 동시성 제어를 위해 Pessimistic Write Lock 사용
     *
     * @param warehouseId 창고 ID
     * @param productCode 상품 코드
     * @return 재고 정보
     */
    Optional<Inventory> findByWarehouseIdAndProductCodeWithLock(UUID warehouseId, String productCode);

    /**
     * 상품코드로 전체 가용 재고 합계 조회
     *
     * @param productCode 상품 코드
     * @return 전체 가용 재고 수량
     */
    Integer getTotalAvailableQuantity(String productCode);
}
