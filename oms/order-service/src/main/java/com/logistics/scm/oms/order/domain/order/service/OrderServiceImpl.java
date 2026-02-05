package com.logistics.scm.oms.order.domain.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.scm.oms.order.domain.order.entity.Order;
import com.logistics.scm.oms.order.domain.order.entity.OrderItem;
import com.logistics.scm.oms.order.domain.order.exception.InvalidOrderStatusException;
import com.logistics.scm.oms.order.domain.order.exception.OrderNotFoundException;
import com.logistics.scm.oms.order.event.order.OrderCancelledEvent;
import com.logistics.scm.oms.order.event.order.OrderCreatedEvent;
import com.logistics.scm.oms.order.domain.order.repository.OrderRepository;
import com.logistics.scm.oms.order.event.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 주문 서비스 구현체
 * 
 * Outbox Pattern 적용:
 * - 비즈니스 데이터(Order)와 이벤트(Outbox)를 동일 트랜잭션으로 저장
 * - Kafka 발행은 별도 Scheduler가 Outbox 테이블을 폴링하여 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        log.info("주문 생성 시작: orderNumber={}", order.getOrderNumber());

        // 1. 주문 저장
        order.setOrderDate(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        // 2. OrderCreatedEvent를 Outbox 테이블에 저장
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(savedOrder.getOrderId().toString())
                .orderNumber(savedOrder.getOrderNumber())
                .customerId(savedOrder.getCustomerId().toString())
                .items(convertToOrderItemEvents(savedOrder.getOrderItems()))
                .totalAmount(savedOrder.getTotalAmount())
                .createdAt(savedOrder.getOrderDate())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxService.saveOutbox(
                    "Order",
                    savedOrder.getOrderId().toString(),
                    "OrderCreatedEvent",
                    payload
            );
            log.info("주문 생성 이벤트 Outbox 저장 완료: orderId={}", savedOrder.getOrderId());
        } catch (Exception e) {
            log.error("주문 생성 이벤트 Outbox 저장 실패: orderId={}", savedOrder.getOrderId(), e);
            throw new RuntimeException("이벤트 저장 실패", e);
        }

        log.info("주문 생성 완료: orderId={}, status={}", 
                savedOrder.getOrderId(), savedOrder.getOrderStatus());

        return savedOrder;
    }

    @Override
    @Transactional
    public Order confirmOrder(UUID orderId) {
        log.info("주문 확정 시작: orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 주문 상태를 CONFIRMED로 변경
        order.confirm();
        Order confirmedOrder = orderRepository.save(order);

        log.info("주문 확정 완료: orderId={}, status={}", 
                confirmedOrder.getOrderId(), confirmedOrder.getOrderStatus());

        return confirmedOrder;
    }

    @Override
    @Transactional
    public Order cancelOrder(UUID orderId, String cancelReason) {
        log.info("주문 취소 시작 (사용자 요청): orderId={}, reason={}", orderId, cancelReason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 주문 취소 가능 여부 확인
        if (order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(orderId, order.getOrderStatus(), "주문 취소");
        }
        if (order.getOrderStatus() == Order.OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException(orderId, order.getOrderStatus(), "주문 취소");
        }

        // 1. 주문 상태를 CANCELLED로 변경
        order.cancel();
        Order cancelledOrder = orderRepository.save(order);

        // 2. OrderCancelledEvent를 Outbox 테이블에 저장
        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(cancelledOrder.getOrderId().toString())
                .orderNumber(cancelledOrder.getOrderNumber())
                .cancelReason(cancelReason)
                .items(convertToOrderCancelledItemEvents(cancelledOrder.getOrderItems()))
                .cancelledAt(LocalDateTime.now())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxService.saveOutbox(
                    "Order",
                    cancelledOrder.getOrderId().toString(),
                    "OrderCancelledEvent",
                    payload
            );
            log.info("주문 취소 이벤트 Outbox 저장 완료: orderId={}", cancelledOrder.getOrderId());
        } catch (Exception e) {
            log.error("주문 취소 이벤트 Outbox 저장 실패: orderId={}", cancelledOrder.getOrderId(), e);
            throw new RuntimeException("이벤트 저장 실패", e);
        }

        log.info("주문 취소 완료: orderId={}, status={}", 
                cancelledOrder.getOrderId(), cancelledOrder.getOrderStatus());

        return cancelledOrder;
    }

    @Override
    @Transactional
    public Order cancelOrderByInventoryFailure(UUID orderId, String reason) {
        log.warn("주문 취소 시작 (재고 부족): orderId={}, reason={}", orderId, reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 주문 상태를 CANCELLED로 변경
        order.cancel();
        Order cancelledOrder = orderRepository.save(order);

        log.info("재고 부족으로 주문 취소 완료: orderId={}, status={}", 
                cancelledOrder.getOrderId(), cancelledOrder.getOrderStatus());

        // OrderCancelledEvent는 발행하지 않음 (재고는 이미 예약되지 않았으므로)

        return cancelledOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Order loadOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    /**
     * OrderItem을 OrderCreatedEvent.OrderItemEvent로 변환
     */
    private List<OrderCreatedEvent.OrderItemEvent> convertToOrderItemEvents(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> OrderCreatedEvent.OrderItemEvent.builder()
                        .productCode(item.getProductCode())
                        .quantity(item.getQuantity())
                        .price(item.getUnitPrice())
                        .warehouseId("550e8400-e29b-41d4-a716-446655440000") // TODO: 실제 창고 ID로 변경
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * OrderItem을 OrderCancelledEvent.OrderItemEvent로 변환
     */
    private List<OrderCancelledEvent.OrderItemEvent> convertToOrderCancelledItemEvents(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> OrderCancelledEvent.OrderItemEvent.builder()
                        .productCode(item.getProductCode())
                        .quantity(item.getQuantity())
                        .warehouseId("550e8400-e29b-41d4-a716-446655440000") // TODO: 실제 창고 ID로 변경
                        .build())
                .collect(Collectors.toList());
    }
}
