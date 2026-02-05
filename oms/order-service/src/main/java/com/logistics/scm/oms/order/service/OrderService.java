package com.logistics.scm.oms.order.service;

import com.logistics.scm.oms.order.entity.Order;

import java.util.UUID;

/**
 * 주문 서비스 인터페이스
 */
public interface OrderService {

    /**
     * 주문 생성
     * 주문을 생성하고 OrderCreatedEvent를 Kafka로 발행
     *
     * @param order 주문 정보
     * @return 생성된 주문
     */
    Order createOrder(Order order);

    /**
     * 주문 확정
     * 재고 예약 성공 시 주문 상태를 CONFIRMED로 업데이트
     *
     * @param orderId 주문 ID
     * @return 확정된 주문
     */
    Order confirmOrder(UUID orderId);

    /**
     * 주문 취소 (사용자 요청)
     * 사용자가 직접 주문을 취소하고 OrderCancelledEvent를 발행
     *
     * @param orderId 주문 ID
     * @param cancelReason 취소 사유
     * @return 취소된 주문
     */
    Order cancelOrder(UUID orderId, String cancelReason);

    /**
     * 주문 취소 (재고 부족)
     * 재고 예약 실패 시 주문을 자동으로 취소 (보상 트랜잭션)
     *
     * @param orderId 주문 ID
     * @param reason 취소 사유
     * @return 취소된 주문
     */
    Order cancelOrderByInventoryFailure(UUID orderId, String reason);

    /**
     * 주문 조회
     *
     * @param orderId 주문 ID
     * @return 주문 정보
     */
    Order getOrder(UUID orderId);
}
