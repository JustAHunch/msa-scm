package com.logistics.scm.oms.inventory.repository;

import com.logistics.scm.oms.inventory.entity.StockMovement;
import com.logistics.scm.oms.inventory.entity.StockMovementType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 재고 이동 이력 Repository Custom 인터페이스
 * 
 * QueryDSL을 사용하는 복잡한 쿼리 메서드 정의
 */
public interface StockMovementRepositoryCustom {

    /**
     * 기간별 재고 이동 이력 조회
     *
     * @param inventoryId 재고 ID
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 이동 이력 목록
     */
    List<StockMovement> findByInventoryIdAndDateRange(UUID inventoryId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 상품코드로 모든 재고의 이동 이력 조회
     *
     * @param productCode 상품 코드
     * @return 이동 이력 목록
     */
    List<StockMovement> findByProductCode(String productCode);

    /**
     * 이동 유형별 통계 조회
     *
     * @param inventoryId 재고 ID
     * @param movementType 이동 유형
     * @return 총 이동 수량
     */
    Integer getTotalQuantityByMovementType(UUID inventoryId, StockMovementType movementType);

    /**
     * 최근 N일간의 이동 이력 조회
     *
     * @param inventoryId 재고 ID
     * @param startDate 시작일
     * @return 이동 이력 목록
     */
    List<StockMovement> findRecentMovements(UUID inventoryId, LocalDateTime startDate);
}
