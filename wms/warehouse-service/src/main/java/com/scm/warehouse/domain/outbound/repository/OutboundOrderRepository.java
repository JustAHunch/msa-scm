package com.scm.warehouse.domain.outbound.repository;

import com.scm.warehouse.entity.OutboundOrder;
import com.scm.warehouse.entity.OutboundOrder.OutboundStatus;
import com.scm.warehouse.entity.OutboundOrder.OutboundType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * OutboundOrderRepository
 * 
 * @author c.h.jo
 * @since 2026-02-06
 */
@Repository
public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, UUID> {

    /**
     * 출고 번호로 조회
     */
    Optional<OutboundOrder> findByOutboundNumber(String outboundNumber);

    /**
     * 주문 번호로 조회
     */
    Optional<OutboundOrder> findByOrderNumber(String orderNumber);

    /**
     * 창고별 출고 주문 목록 조회
     */
    List<OutboundOrder> findByWarehouseId(UUID warehouseId);

    /**
     * 창고 + 상태별 조회
     */
    List<OutboundOrder> findByWarehouseIdAndOutboundStatus(UUID warehouseId, OutboundStatus status);

    /**
     * 창고 + 타입별 조회
     */
    List<OutboundOrder> findByWarehouseIdAndOutboundType(UUID warehouseId, OutboundType type);

    /**
     * 목적지 창고별 조회 (TO 타입용)
     */
    List<OutboundOrder> findByDestinationWarehouseId(UUID destinationWarehouseId);

    /**
     * 예정일 기준 조회
     */
    List<OutboundOrder> findByScheduledDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 상태별 출고 주문 수 조회
     */
    long countByOutboundStatus(OutboundStatus status);
}
