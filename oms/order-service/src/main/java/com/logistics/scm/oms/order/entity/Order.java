package com.logistics.scm.oms.order.entity;

import com.logistics.scm.oms.order.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * @since 2026-01-27
 */
@Schema(description = "주문 정보")
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

    @Schema(description = "주문 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", columnDefinition = "uuid")
    private UUID orderId;

    @Schema(description = "주문 번호", example = "ORD-2025-00001", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Schema(description = "고객 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440001", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "customer_id", nullable = false, columnDefinition = "uuid")
    private UUID customerId;

    @Schema(description = "주문 상태", example = "CREATED", requiredMode = Schema.RequiredMode.REQUIRED, 
            allowableValues = {"CREATED", "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"})
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Schema(description = "총 주문 금액", example = "150000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Schema(description = "주문 일시", example = "2025-01-28T10:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Schema(description = "주문 상품 목록")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Enum for Order Status
    @Schema(description = "주문 상태 코드")
    public enum OrderStatus {
        @Schema(description = "주문 생성") CREATED,
        @Schema(description = "주문 접수") PENDING,
        @Schema(description = "주문 확정") CONFIRMED,
        @Schema(description = "출고 완료") SHIPPED,
        @Schema(description = "배송 완료") DELIVERED,
        @Schema(description = "주문 취소") CANCELLED
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
