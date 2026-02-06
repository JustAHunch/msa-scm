package com.logistics.scm.oms.inventory.domain.inbound.repository;

import com.logistics.scm.oms.inventory.domain.inbound.entity.Inbound;
import com.logistics.scm.oms.inventory.domain.inbound.entity.InboundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 입고 Repository
 */
@Repository
public interface InboundRepository extends JpaRepository<Inbound, UUID> {

    /**
     * 입고 번호로 조회
     */
    Optional<Inbound> findByInboundNumber(String inboundNumber);

    /**
     * 상태별 입고 목록 조회
     */
    List<Inbound> findByStatus(InboundStatus status);

    /**
     * 창고별 입고 목록 조회
     */
    List<Inbound> findByWarehouseId(UUID warehouseId);

    /**
     * 업체별 입고 목록 조회
     */
    List<Inbound> findByCompanyId(UUID companyId);

    /**
     * 창고 + 상태별 입고 목록 조회
     */
    List<Inbound> findByWarehouseIdAndStatus(UUID warehouseId, InboundStatus status);

    /**
     * 입고 번호 존재 여부
     */
    boolean existsByInboundNumber(String inboundNumber);
}
