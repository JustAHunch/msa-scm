package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Zone Entity
 * 
 * 창고 내 구역 정보를 관리합니다.
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Entity
@Table(name = "ZONE_TB",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_warehouse_zone", columnNames = {"warehouse_id", "zone_code"})
    },
    indexes = {
        @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
        @Index(name = "idx_zone_type", columnList = "zone_type")
    }
)
public class Zone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "zone_id", columnDefinition = "uuid")
    private UUID zoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_zone_warehouse"))
    private Warehouse warehouse;

    @Column(name = "zone_code", nullable = false, length = 50)
    private String zoneCode;

    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName;

    @Enumerated(EnumType.STRING)
    @Column(name = "zone_type", nullable = false, length = 20)
    private ZoneType zoneType;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();

    // Enum for Zone Type
    public enum ZoneType {
        RECEIVING,  // 입고 구역
        STORAGE,    // 보관 구역
        PICKING,    // 피킹 구역
        PACKING,    // 포장 구역
        SHIPPING    // 출고 구역
    }

    // Constructors
    protected Zone() {
    }

    public Zone(Warehouse warehouse, String zoneCode, String zoneName, ZoneType zoneType, Integer capacity) {
        this.warehouse = warehouse;
        this.zoneCode = zoneCode;
        this.zoneName = zoneName;
        this.zoneType = zoneType;
        this.capacity = capacity;
        this.isActive = true;
    }

    // Getters and Setters
    public UUID getZoneId() {
        return zoneId;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public ZoneType getZoneType() {
        return zoneType;
    }

    public void setZoneType(ZoneType zoneType) {
        this.zoneType = zoneType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
