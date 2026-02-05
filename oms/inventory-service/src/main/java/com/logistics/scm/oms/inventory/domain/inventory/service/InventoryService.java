package com.logistics.scm.oms.inventory.domain.inventory.service;

import com.logistics.scm.oms.inventory.domain.inventory.dto.request.ReleaseStockRequest;
import com.logistics.scm.oms.inventory.domain.inventory.dto.request.ReserveStockRequest;
import com.logistics.scm.oms.inventory.domain.inventory.dto.response.InventoryResponse;

import java.util.List;
import java.util.UUID;

/**
 * 재고 서비스 인터페이스
 */
public interface InventoryService {

    /**
     * 재고 예약 (차감)
     * 주문 생성 시 재고를 예약하여 가용수량을 감소시키고 할당수량을 증가시킴
     *
     * @param request 재고 예약 요청
     * @return 재고 응답 DTO
     */
    InventoryResponse reserveStock(ReserveStockRequest request);

    /**
     * 재고 해제 (원복)
     * 주문 취소 시 예약된 재고를 해제하여 가용수량을 증가시키고 할당수량을 감소시킴
     *
     * @param request 재고 해제 요청
     * @return 재고 응답 DTO
     */
    InventoryResponse releaseStock(ReleaseStockRequest request);

    /**
     * 재고 확인
     * 주문 가능 여부를 확인하기 위해 재고를 조회
     *
     * @param warehouseId 창고 ID
     * @param productCode 상품 코드
     * @param quantity 요청 수량
     * @return 재고 충분 여부
     */
    boolean checkStock(UUID warehouseId, String productCode, Integer quantity);

    /**
     * 재고 조회 (창고ID + 상품코드)
     *
     * @param warehouseId 창고 ID
     * @param productCode 상품 코드
     * @return 재고 응답 DTO
     */
    InventoryResponse getInventory(UUID warehouseId, String productCode);

    /**
     * 상품코드로 모든 창고의 재고 조회
     *
     * @param productCode 상품 코드
     * @return 재고 목록
     */
    List<InventoryResponse> getInventoriesByProductCode(String productCode);

    /**
     * 창고ID로 모든 재고 조회
     *
     * @param warehouseId 창고 ID
     * @return 재고 목록
     */
    List<InventoryResponse> getInventoriesByWarehouse(UUID warehouseId);

    /**
     * 재고가 부족한 상품 목록 조회
     * 안전재고 미만인 상품들을 조회
     *
     * @return 재고 부족 상품 목록
     */
    List<InventoryResponse> getLowStockInventories();

    /**
     * 재고 생성 또는 업데이트
     * 재고가 없으면 생성하고, 있으면 수량을 증가시킴
     *
     * @param warehouseId 창고 ID
     * @param productCode 상품 코드
     * @param quantity 수량
     * @param safetyStock 안전 재고
     * @return 재고 응답 DTO
     */
    InventoryResponse createOrUpdateInventory(UUID warehouseId, String productCode,
                                              Integer quantity, Integer safetyStock);
}
