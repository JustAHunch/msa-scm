package com.scm.warehouse.entity;

import com.scm.warehouse.common.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PickingList Entity - 피킹 리스트
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Entity
@Table(name = "PICKING_LIST_TB", indexes = {
    @Index(name = "idx_picking_number", columnList = "picking_number", unique = true),
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
    @Index(name = "idx_picking_status", columnList = "picking_status")
})
public class PickingList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "picking_list_id", columnDefinition = "uuid")
    private UUID pickingListId;

    @Column(name = "picking_number", nullable = false, unique = true, length = 50)
    private String pickingNumber;

    @Column(name = "order_id", nullable = false, columnDefinition = "uuid")
    private UUID orderId;

    @Column(name = "warehouse_id", nullable = false, columnDefinition = "uuid")
    private UUID warehouseId;

    @Column(name = "assigned_worker_id", columnDefinition = "uuid")
    private UUID assignedWorkerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "picking_status", nullable = false, length = 20)
    private PickingStatus pickingStatus;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "pickingList", cascade = CascadeType.ALL)
    private List<PickingItem> pickingItems = new ArrayList<>();

    public enum PickingStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }

    protected PickingList() {
    }

    public PickingList(String pickingNumber, UUID orderId, UUID warehouseId, Integer priority) {
        this.pickingNumber = pickingNumber;
        this.orderId = orderId;
        this.warehouseId = warehouseId;
        this.priority = priority;
        this.pickingStatus = PickingStatus.PENDING;
    }

    // Getters and Setters
    public UUID getPickingListId() {
        return pickingListId;
    }

    public String getPickingNumber() {
        return pickingNumber;
    }

    public void setPickingNumber(String pickingNumber) {
        this.pickingNumber = pickingNumber;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(UUID warehouseId) {
        this.warehouseId = warehouseId;
    }

    public UUID getAssignedWorkerId() {
        return assignedWorkerId;
    }

    public void setAssignedWorkerId(UUID assignedWorkerId) {
        this.assignedWorkerId = assignedWorkerId;
    }

    public PickingStatus getPickingStatus() {
        return pickingStatus;
    }

    public void setPickingStatus(PickingStatus pickingStatus) {
        this.pickingStatus = pickingStatus;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<PickingItem> getPickingItems() {
        return pickingItems;
    }

    public void setPickingItems(List<PickingItem> pickingItems) {
        this.pickingItems = pickingItems;
    }
}
