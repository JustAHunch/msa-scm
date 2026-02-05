package com.scm.delivery.entity;

import com.scm.delivery.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * VehicleLocation Entity - 차량 위치 추적
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "VEHICLE_LOCATION_TB", indexes = {
    @Index(name = "idx_vehicle_id", columnList = "vehicle_id"),
    @Index(name = "idx_recorded_at", columnList = "recorded_at")
})
public class VehicleLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "location_id", columnDefinition = "uuid")
    private UUID locationId;

    @Column(name = "vehicle_id", nullable = false, columnDefinition = "uuid")
    private UUID vehicleId;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "speed", precision = 5, scale = 2)
    private BigDecimal speed;

}
