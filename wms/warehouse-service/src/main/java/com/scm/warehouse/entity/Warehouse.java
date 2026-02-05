package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Warehouse Entity
 * 
 * 창고 정보를 관리합니다.
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Entity
@Table(name = "WAREHOUSE_TB", indexes = {
    @Index(name = "idx_warehouse_code", columnList = "warehouse_code", unique = true),
    @Index(name = "idx_warehouse_type", columnList = "warehouse_type"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "warehouse_id", columnDefinition = "uuid")
    private UUID warehouseId;

    @Column(name = "warehouse_code", nullable = false, unique = true, length = 50)
    private String warehouseCode;

    @Column(name = "warehouse_name", nullable = false, length = 100)
    private String warehouseName;

    @Column(name = "address", length = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_type", nullable = false, length = 20)
    private WarehouseType warehouseType;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Zone> zones = new ArrayList<>();

    // Enum for Warehouse Type
    public enum WarehouseType {
        DC,    // Distribution Center
        FC,    // Fulfillment Center
        HUB    // Hub
    }

    // Constructors
    protected Warehouse() {
    }

    public Warehouse(String warehouseCode, String warehouseName, String address, 
                     WarehouseType warehouseType, Integer capacity) {
        this.warehouseCode = warehouseCode;
        this.warehouseName = warehouseName;
        this.address = address;
        this.warehouseType = warehouseType;
        this.capacity = capacity;
        this.isActive = true;
    }

    // Business Methods
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Getters and Setters
    public UUID getWarehouseId() {
        return warehouseId;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public WarehouseType getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(WarehouseType warehouseType) {
        this.warehouseType = warehouseType;
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

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }
}
