package com.scm.delivery.entity;

import com.scm.delivery.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DeliveryRoute Entity - 배송 경로
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DELIVERY_ROUTE_TB", indexes = {
    @Index(name = "idx_vehicle_id", columnList = "vehicle_id"),
    @Index(name = "idx_driver_id", columnList = "driver_id"),
    @Index(name = "idx_route_date", columnList = "route_date"),
    @Index(name = "idx_route_status", columnList = "route_status")
})
public class DeliveryRoute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "route_id", columnDefinition = "uuid")
    private UUID routeId;

    @Column(name = "route_name", nullable = false, length = 100)
    private String routeName;

    @Column(name = "vehicle_id", nullable = false, columnDefinition = "uuid")
    private UUID vehicleId;

    @Column(name = "driver_id", nullable = false, columnDefinition = "uuid")
    private UUID driverId;

    @Column(name = "route_date", nullable = false)
    private LocalDate routeDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_distance", precision = 10, scale = 2)
    private BigDecimal totalDistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_status", nullable = false, length = 20)
    private RouteStatus routeStatus;

    public enum RouteStatus {
        PLANNED,      // 계획됨
        IN_PROGRESS,  // 진행 중
        COMPLETED     // 완료
    }

    // Business Methods
    public void start() {
        this.routeStatus = RouteStatus.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
    }

    public void complete(BigDecimal totalDistance) {
        this.routeStatus = RouteStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
        this.totalDistance = totalDistance;
    }

}
