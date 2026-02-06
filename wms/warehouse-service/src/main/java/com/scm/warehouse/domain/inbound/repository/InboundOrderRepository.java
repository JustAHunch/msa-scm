package com.scm.warehouse.domain.inbound.repository;

import com.scm.warehouse.entity.InboundOrder;
import com.scm.warehouse.entity.InboundOrder.InboundStatus;
import com.scm.warehouse.entity.InboundOrder.InboundType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * InboundOrderRepository
 * 
 * @author c.h.jo
 * @since 2026-02-06
 */
@Repository
public interface InboundOrderRepository extends JpaRepository<InboundOrder, UUID> {

    /**
     * 입고 번호로 조회
     */
    Optional<InboundOrder> findByInboundNumber(String inboundNumber);

    /**
     * 창고별 입고 주문 목록 조회
     */
    List<InboundOrder> findByWarehouseId(UUID warehouseId);

    /**
     * 창고 + 상태별 조회
     */
    List<InboundOrder> findByWarehouseIdAndInboundStatus(UUID warehouseId, InboundStatus status);

    /**
     * 창고 + 타입별 조회
     */
    List<InboundOrder> findByWarehouseIdAndInboundType(UUID warehouseId, InboundType type);

    /**
     * 예정일 기준 조회
     */
    List<InboundOrder> findByExpectedDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 상태별 입고 주문 수 조회
     */
    long countByInboundStatus(InboundStatus status);
}
