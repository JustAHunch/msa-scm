package com.scm.delivery.entity;

import com.scm.delivery.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DeliveryOrder Entity - 배송 주문
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DELIVERY_ORDER_TB", indexes = {
    @Index(name = "idx_delivery_number", columnList = "delivery_number", unique = true),
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_delivery_status", columnList = "delivery_status")
})
public class DeliveryOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id", columnDefinition = "uuid")
    private UUID deliveryId;

    @Column(name = "delivery_number", nullable = false, unique = true, length = 50)
    private String deliveryNumber;

    @Column(name = "order_id", nullable = false, columnDefinition = "uuid")
    private UUID orderId;

    @Column(name = "vehicle_id", columnDefinition = "uuid")
    private UUID vehicleId;

    @Column(name = "driver_id", columnDefinition = "uuid")
    private UUID driverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    private DeliveryStatus deliveryStatus;

    @Column(name = "pickup_address", length = 500)
    private String pickupAddress;

    @Column(name = "delivery_address", nullable = false, length = 500)
    private String deliveryAddress;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    public enum DeliveryStatus {
        SCHEDULED, IN_TRANSIT, DELIVERED, FAILED
    }

}
