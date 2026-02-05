package com.logistics.scm.oms.inventory.repository;

import com.logistics.scm.oms.inventory.entity.Inventory;
import com.logistics.scm.oms.inventory.entity.QInventory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 재고 Repository Custom 구현체
 * 
 * QueryDSL을 사용한 복잡한 쿼리 구현
 */
@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Inventory> findLowStockInventories() {
        QInventory inventory = QInventory.inventory;

        return queryFactory
                .selectFrom(inventory)
                .where(inventory.availableQty.lt(inventory.safetyStock))
                .fetch();
    }

    @Override
    public Optional<Inventory> findByWarehouseIdAndProductCodeWithLock(UUID warehouseId, String productCode) {
        QInventory inventory = QInventory.inventory;

        Inventory result = queryFactory
                .selectFrom(inventory)
                .where(
                        inventory.warehouseId.eq(warehouseId),
                        inventory.productCode.eq(productCode)
                )
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Integer getTotalAvailableQuantity(String productCode) {
        QInventory inventory = QInventory.inventory;

        Integer total = queryFactory
                .select(inventory.availableQty.sum())
                .from(inventory)
                .where(inventory.productCode.eq(productCode))
                .fetchOne();

        return total != null ? total : 0;
    }
}
