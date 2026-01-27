package com.scm.analytics.document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WarehouseOperations Document - 창고 작업 데이터
 * 
 * MongoDB Collection: warehouse_operations
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "warehouse_operations")
public class WarehouseOperations {

    @Id
    private String id;

    @Field("operation_type")
    private String operationType;  // PICKING, PACKING, RECEIVING

    @Field("warehouse_id")
    private UUID warehouseId;

    @Field("worker_id")
    private UUID workerId;

    @Field("operation_date")
    private LocalDateTime operationDate;

    @Field("items_processed")
    private Integer itemsProcessed;

    @Field("time_taken")
    private Integer timeTaken;  // 작업 시간 (분)

    @Field("created_at")
    private LocalDateTime createdAt;
}
