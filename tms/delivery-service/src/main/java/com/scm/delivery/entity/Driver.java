package com.scm.delivery.entity;

import com.scm.delivery.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Driver Entity - 배송 기사
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DRIVER_TB", indexes = {
    @Index(name = "idx_driver_code", columnList = "driver_code", unique = true),
    @Index(name = "idx_driver_status", columnList = "driver_status"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
public class Driver extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "driver_id", columnDefinition = "uuid")
    private UUID driverId;

    @Column(name = "driver_code", nullable = false, unique = true, length = 50)
    private String driverCode;

    @Column(name = "driver_name", nullable = false, length = 100)
    private String driverName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "license_number", nullable = false, length = 50)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "driver_status", nullable = false, length = 20)
    private DriverStatus driverStatus;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public enum DriverStatus {
        AVAILABLE,    // 배차 가능
        ON_DELIVERY,  // 배송 중
        OFF_DUTY      // 휴무
    }

    // Business Methods
    public void startDelivery() {
        this.driverStatus = DriverStatus.ON_DELIVERY;
    }

    public void endDelivery() {
        this.driverStatus = DriverStatus.AVAILABLE;
    }

    public void goOffDuty() {
        this.driverStatus = DriverStatus.OFF_DUTY;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

}
