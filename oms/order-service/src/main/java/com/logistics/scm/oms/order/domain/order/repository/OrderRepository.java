package com.logistics.scm.oms.order.domain.order.repository;

import com.logistics.scm.oms.order.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 주문 Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * 주문 번호로 주문 조회
     *
     * @param orderNumber 주문 번호
     * @return 주문 정보
     */
    Optional<Order> findByOrderNumber(String orderNumber);
}
