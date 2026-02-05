package com.logistics.scm.oms.inventory.repository;

import com.logistics.scm.oms.inventory.entity.QInventory;
import com.logistics.scm.oms.inventory.entity.QStockMovement;
import com.logistics.scm.oms.inventory.entity.StockMovement;
import com.logistics.scm.oms.inventory.entity.StockMovementType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 재고 이동 이력 Repository Custom 구현체
 * 
 * QueryDSL을 사용한 복잡한 쿼리 구현
 */
@Repository
@RequiredArgsConstructor
public class StockMovementRepositoryImpl implements StockMovementRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StockMovement> findByInventoryIdAndDateRange(UUID inventoryId, LocalDateTime startDate, LocalDateTime endDate) {
        QStockMovement stockMovement = QStockMovement.stockMovement;

        return queryFactory
                .selectFrom(stockMovement)
                .where(
                        stockMovement.inventoryId.eq(inventoryId),
                        stockMovement.createdAt.between(startDate, endDate)
                )
                .orderBy(stockMovement.createdAt.desc())
                .fetch();
    }

    @Override
    public List<StockMovement> findByProductCode(String productCode) {
        QStockMovement stockMovement = QStockMovement.stockMovement;
        QInventory inventory = QInventory.inventory;

        return queryFactory
                .selectFrom(stockMovement)
                .join(inventory).on(stockMovement.inventoryId.eq(inventory.inventoryId))
                .where(inventory.productCode.eq(productCode))
                .orderBy(stockMovement.createdAt.desc())
                .fetch();
    }

    @Override
    public Integer getTotalQuantityByMovementType(UUID inventoryId, StockMovementType movementType) {
        QStockMovement stockMovement = QStockMovement.stockMovement;

        Integer total = queryFactory
                .select(stockMovement.quantity.sum())
                .from(stockMovement)
                .where(
                        stockMovement.inventoryId.eq(inventoryId),
                        stockMovement.movementType.eq(movementType)
                )
                .fetchOne();

        return total != null ? total : 0;
    }

    @Override
    public List<StockMovement> findRecentMovements(UUID inventoryId, LocalDateTime startDate) {
        QStockMovement stockMovement = QStockMovement.stockMovement;

        return queryFactory
                .selectFrom(stockMovement)
                .where(
                        stockMovement.inventoryId.eq(inventoryId),
                        stockMovement.createdAt.goe(startDate)
                )
                .orderBy(stockMovement.createdAt.desc())
                .fetch();
    }
}
