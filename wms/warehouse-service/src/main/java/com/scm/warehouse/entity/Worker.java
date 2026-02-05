package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Worker Entity - 창고 작업자
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Entity
@Table(name = "WORKER_TB", indexes = {
    @Index(name = "idx_worker_code", columnList = "worker_code", unique = true),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_role", columnList = "role"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
public class Worker extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "worker_id", columnDefinition = "uuid")
    private UUID workerId;

    @Column(name = "worker_code", nullable = false, unique = true, length = 50)
    private String workerCode;

    @Column(name = "worker_name", nullable = false, length = 100)
    private String workerName;

    @Column(name = "warehouse_id", nullable = false, columnDefinition = "uuid")
    private UUID warehouseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private WorkerRole role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public enum WorkerRole {
        PICKER,   // 피커
        PACKER,   // 패커
        QC,       // 품질 검사
        MANAGER   // 관리자
    }

    protected Worker() {
    }

    public Worker(String workerCode, String workerName, UUID warehouseId, WorkerRole role) {
        this.workerCode = workerCode;
        this.workerName = workerName;
        this.warehouseId = warehouseId;
        this.role = role;
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
    public UUID getWorkerId() {
        return workerId;
    }

    public String getWorkerCode() {
        return workerCode;
    }

    public void setWorkerCode(String workerCode) {
        this.workerCode = workerCode;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public WorkerRole getRole() {
        return role;
    }

    public void setRole(WorkerRole role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
