package com.logistics.scm.oms.order.service;

import com.logistics.scm.oms.order.entity.Order;
import com.logistics.scm.oms.order.entity.OrderItem;
import com.logistics.scm.oms.order.event.OrderCancelledEvent;
import com.logistics.scm.oms.order.event.OrderCreatedEvent;
import com.logistics.scm.oms.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 주문 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderCreatedEvent> orderCreatedEventKafkaTemplate;
    private final KafkaTemplate<String, OrderCancelledEvent> orderCancelledEventKafkaTemplate;

    @Value("${kafka.topics.order-events}")
    private String orderEventsTopic;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        log.info("주문 생성 시작: orderNumber={}", order.getOrderNumber());

        // 주문 저장
        order.setOrderDate(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        // OrderCreatedEvent 발행
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(savedOrder.getOrderId().toString())
                .orderNumber(savedOrder.getOrderNumber())
                .customerId(savedOrder.getCustomerId().toString())
                .items(convertToOrderItemEvents(savedOrder.getOrderItems()))
                .totalAmount(savedOrder.getTotalAmount())
                .createdAt(savedOrder.getOrderDate())
                .build();

        orderCreatedEventKafkaTemplate.send(orderEventsTopic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("주문 생성 이벤트 발행 완료: orderId={}, orderNumber={}", 
                                savedOrder.getOrderId(), savedOrder.getOrderNumber());
                    } else {
                        log.error("주문 생성 이벤트 발행 실패: orderId={}", 
                                savedOrder.getOrderId(), ex);
                    }
                });

        log.info("주문 생성 완료: orderId={}, status={}", 
                savedOrder.getOrderId(), savedOrder.getOrderStatus());

        return savedOrder;
    }

    @Override
    @Transactional
    public Order confirmOrder(UUID orderId) {
        log.info("주문 확정 시작: orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

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
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        // 주문 취소 가능 여부 확인
        if (order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 주문입니다");
        }
        if (order.getOrderStatus() == Order.OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송 완료된 주문은 취소할 수 없습니다");
        }

        // 주문 상태를 CANCELLED로 변경
        order.cancel();
        Order cancelledOrder = orderRepository.save(order);

        // OrderCancelledEvent 발행 (재고 원복 위해)
        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(cancelledOrder.getOrderId().toString())
                .orderNumber(cancelledOrder.getOrderNumber())
                .cancelReason(cancelReason)
                .items(convertToOrderCancelledItemEvents(cancelledOrder.getOrderItems()))
                .cancelledAt(LocalDateTime.now())
                .build();

        orderCancelledEventKafkaTemplate.send(orderEventsTopic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("주문 취소 이벤트 발행 완료: orderId={}", cancelledOrder.getOrderId());
                    } else {
                        log.error("주문 취소 이벤트 발행 실패: orderId={}", cancelledOrder.getOrderId(), ex);
                    }
                });

        log.info("주문 취소 완료: orderId={}, status={}", 
                cancelledOrder.getOrderId(), cancelledOrder.getOrderStatus());

        return cancelledOrder;
    }

    @Override
    @Transactional
    public Order cancelOrderByInventoryFailure(UUID orderId, String reason) {
        log.warn("주문 취소 시작 (재고 부족): orderId={}, reason={}", orderId, reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

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
    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
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
