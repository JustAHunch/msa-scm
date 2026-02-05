package com.logistics.scm.oms.inventory.domain.inventory.service;

import com.logistics.scm.oms.inventory.domain.inventory.dto.request.ReleaseStockRequest;
import com.logistics.scm.oms.inventory.domain.inventory.dto.request.ReserveStockRequest;
import com.logistics.scm.oms.inventory.domain.inventory.dto.response.InventoryResponse;
import com.logistics.scm.oms.inventory.domain.inventory.entity.Inventory;
import com.logistics.scm.oms.inventory.domain.stockmovement.entity.StockMovement;
import com.logistics.scm.oms.inventory.domain.stockmovement.entity.StockMovementType;
import com.logistics.scm.oms.inventory.domain.inventory.exception.InsufficientStockException;
import com.logistics.scm.oms.inventory.domain.inventory.exception.InventoryNotFoundException;
import com.logistics.scm.oms.inventory.domain.inventory.respository.InventoryRepository;
import com.logistics.scm.oms.inventory.domain.stockmovement.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 재고 서비스 구현체
 *
 * 재고 관리 핵심 비즈니스 로직
 * - 재고 예약/해제
 * - 동시성 제어 (Pessimistic Lock)
 * - 재고 이동 이력 기록
 * - Redis 캐싱
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional
    @CacheEvict(value = "inventory", key = "#request.warehouseId + '_' + #request.productCode")
    public InventoryResponse reserveStock(ReserveStockRequest request) {
        log.info("재고 예약 시작: warehouseId={}, productCode={}, quantity={}, orderId={}",
                request.getWarehouseId(), request.getProductCode(), 
                request.getQuantity(), request.getReferenceOrderId());

        // 1. 비관적 락을 사용하여 재고 조회 (동시성 제어)
        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductCodeWithLock(request.getWarehouseId(), request.getProductCode())
                .orElseThrow(() -> new InventoryNotFoundException(
                        request.getWarehouseId(), request.getProductCode()));

        // 2. 재고 충분 여부 확인
        if (inventory.getAvailableQty() < request.getQuantity()) {
            throw new InsufficientStockException(
                    request.getProductCode(),
                    request.getQuantity(),
                    inventory.getAvailableQty()
            );
        }

        // 3. 재고 차감 (가용수량 감소, 할당수량 증가)
        inventory.reserve(request.getQuantity());
        Inventory savedInventory = inventoryRepository.save(inventory);

        // 4. 재고 이동 이력 기록
        StockMovement movement = StockMovement.builder()
                .inventoryId(inventory.getInventoryId())
                .movementType(StockMovementType.RESERVED)
                .quantity(request.getQuantity())
                .referenceOrderId(request.getReferenceOrderId())
                .remarks(request.getRemarks())
                .build();
        stockMovementRepository.save(movement);

        log.info("재고 예약 완료: inventoryId={}, availableQty={}, allocatedQty={}",
                savedInventory.getInventoryId(), 
                savedInventory.getAvailableQty(),
                savedInventory.getAllocatedQty());

        return InventoryResponse.from(savedInventory);
    }

    @Override
    @Transactional
    @CacheEvict(value = "inventory", key = "#request.warehouseId + '_' + #request.productCode")
    public InventoryResponse releaseStock(ReleaseStockRequest request) {
        log.info("재고 해제 시작: warehouseId={}, productCode={}, quantity={}, orderId={}",
                request.getWarehouseId(), request.getProductCode(), 
                request.getQuantity(), request.getReferenceOrderId());

        // 1. 비관적 락을 사용하여 재고 조회
        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductCodeWithLock(request.getWarehouseId(), request.getProductCode())
                .orElseThrow(() -> new InventoryNotFoundException(
                        request.getWarehouseId(), request.getProductCode()));

        // 2. 재고 원복 (가용수량 증가, 할당수량 감소)
        inventory.release(request.getQuantity());
        Inventory savedInventory = inventoryRepository.save(inventory);

        // 3. 재고 이동 이력 기록
        StockMovement movement = StockMovement.builder()
                .inventoryId(inventory.getInventoryId())
                .movementType(StockMovementType.RELEASED)
                .quantity(request.getQuantity())
                .referenceOrderId(request.getReferenceOrderId())
                .remarks(request.getRemarks())
                .build();
        stockMovementRepository.save(movement);

        log.info("재고 해제 완료: inventoryId={}, availableQty={}, allocatedQty={}",
                savedInventory.getInventoryId(), 
                savedInventory.getAvailableQty(),
                savedInventory.getAllocatedQty());

        return InventoryResponse.from(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "inventory", key = "#warehouseId + '_' + #productCode")
    public boolean checkStock(UUID warehouseId, String productCode, Integer quantity) {
        log.debug("재고 확인: warehouseId={}, productCode={}, requestedQty={}",
                warehouseId, productCode, quantity);

        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductCode(warehouseId, productCode)
                .orElse(null);

        if (inventory == null) {
            log.warn("재고 없음: warehouseId={}, productCode={}", warehouseId, productCode);
            return false;
        }

        boolean isAvailable = inventory.getAvailableQty() >= quantity;
        log.debug("재고 확인 결과: available={}, requestedQty={}, availableQty={}",
                isAvailable, quantity, inventory.getAvailableQty());

        return isAvailable;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "inventory", key = "#warehouseId + '_' + #productCode")
    public InventoryResponse getInventory(UUID warehouseId, String productCode) {
        log.debug("재고 조회: warehouseId={}, productCode={}", warehouseId, productCode);

        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductCode(warehouseId, productCode)
                .orElseThrow(() -> new InventoryNotFoundException(warehouseId, productCode));

        return InventoryResponse.from(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoriesByProductCode(String productCode) {
        log.debug("상품별 재고 조회: productCode={}", productCode);

        return inventoryRepository.findByProductCode(productCode).stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoriesByWarehouse(UUID warehouseId) {
        log.debug("창고별 재고 조회: warehouseId={}", warehouseId);

        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockInventories() {
        log.debug("재고 부족 상품 조회");

        return inventoryRepository.findLowStockInventories().stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "inventory", key = "#warehouseId + '_' + #productCode")
    public InventoryResponse createOrUpdateInventory(UUID warehouseId, String productCode,
                                                     Integer quantity, Integer safetyStock) {
        log.info("재고 생성/업데이트: warehouseId={}, productCode={}, quantity={}, safetyStock={}",
                warehouseId, productCode, quantity, safetyStock);

        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductCode(warehouseId, productCode)
                .orElse(null);

        if (inventory == null) {
            // 재고 신규 생성
            inventory = Inventory.builder()
                    .warehouseId(warehouseId)
                    .productCode(productCode)
                    .availableQty(quantity)
                    .allocatedQty(0)
                    .totalQty(quantity)
                    .safetyStock(safetyStock != null ? safetyStock : 0)
                    .lastUpdated(LocalDateTime.now())
                    .build();

            log.info("재고 신규 생성: productCode={}", productCode);
        } else {
            // 기존 재고 수량 증가
            inventory.increaseAvailableQuantity(quantity);
            if (safetyStock != null) {
                inventory.updateSafetyStock(safetyStock);
            }

            log.info("재고 업데이트: productCode={}, newAvailableQty={}",
                    productCode, inventory.getAvailableQty());
        }

        Inventory savedInventory = inventoryRepository.save(inventory);

        // 재고 이동 이력 기록 (입고)
        StockMovement movement = StockMovement.builder()
                .inventoryId(savedInventory.getInventoryId())
                .movementType(StockMovementType.INBOUND)
                .quantity(quantity)
                .referenceOrderId("MANUAL")
                .remarks("재고 생성/업데이트")
                .build();
        stockMovementRepository.save(movement);

        return InventoryResponse.from(savedInventory);
    }
}
