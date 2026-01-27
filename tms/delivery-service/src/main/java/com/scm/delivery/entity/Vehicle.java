package com.scm.delivery.entity;

import com.scm.delivery.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Vehicle Entity - 배송 차량
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "VEHICLE_TB", indexes = {
    @Index(name = "idx_vehicle_number", columnList = "vehicle_number", unique = true),
    @Index(name = "idx_vehicle_type", columnList = "vehicle_type"),
    @Index(name = "idx_current_status", columnList = "current_status")
})
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "vehicle_id", columnDefinition = "uuid")
    private UUID vehicleId;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 50)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType vehicleType;

    @Column(name = "capacity", precision = 10, scale = 2)
    private BigDecimal capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 20)
    private VehicleStatus currentStatus;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public enum VehicleType { 
        TRUCK,      // 트럭
        VAN,        // 밴
        MOTORCYCLE  // 오토바이
    }
    
    public enum VehicleStatus { 
        AVAILABLE,   // 사용 가능
        IN_USE,      // 사용 중
        MAINTENANCE  // 정비 중
    }

    // Business Methods
    public void startUse() {
        this.currentStatus = VehicleStatus.IN_USE;
    }

    public void endUse() {
        this.currentStatus = VehicleStatus.AVAILABLE;
    }

    public void startMaintenance() {
        this.currentStatus = VehicleStatus.MAINTENANCE;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

}
