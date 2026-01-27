package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Location Entity
 * 
 * 창고 내 로케이션(적치 위치) 정보를 관리합니다.
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "LOCATION_TB",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_zone_location", columnNames = {"zone_id", "location_code"})
    },
    indexes = {
        @Index(name = "idx_zone_id", columnList = "zone_id"),
        @Index(name = "idx_location_type", columnList = "location_type"),
        @Index(name = "idx_is_occupied", columnList = "is_occupied")
    }
)
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "location_id", columnDefinition = "uuid")
    private UUID locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false, foreignKey = @ForeignKey(name = "fk_location_zone"))
    private Zone zone;

    @Column(name = "location_code", nullable = false, length = 50)
    private String locationCode;  // A-01-01-01 (통로-열-단-층)

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false, length = 20)
    private LocationType locationType;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_occupied", nullable = false)
    private Boolean isOccupied;

    // Enum for Location Type
    public enum LocationType {
        SHELF,   // 선반
        PALLET,  // 팔레트
        FLOOR    // 바닥
    }

    // Business Methods
    public void occupy() {
        this.isOccupied = true;
    }

    public void vacate() {
        this.isOccupied = false;
    }

}
