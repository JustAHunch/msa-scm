package com.logistics.scm.oms.order.entity;

import com.logistics.scm.oms.order.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order Entity
 * 
 * 주문 정보를 관리합니다.
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ORDER_TB", indexes = {
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_order_status", columnList = "order_status"),
    @Index(name = "idx_order_date", columnList = "order_date")
})
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", columnDefinition = "uuid")
    private UUID orderId;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false, columnDefinition = "uuid")
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Enum for Order Status
    public enum OrderStatus {
        PENDING,      // 주문 접수
        CONFIRMED,    // 주문 확정
        SHIPPED,      // 출고 완료
        DELIVERED,    // 배송 완료
        CANCELLED     // 주문 취소
    }

    // Business Methods
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    public void confirm() {
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void ship() {
        this.orderStatus = OrderStatus.SHIPPED;
    }

    public void deliver() {
        this.orderStatus = OrderStatus.DELIVERED;
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
    }
}
