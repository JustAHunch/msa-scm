package com.scm.analytics.document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OrderAnalytics Document - 주문 분석 데이터
 * 
 * MongoDB Collection: order_analytics
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "order_analytics")
public class OrderAnalytics {

    @Id
    private String id;

    @Field("order_id")
    private UUID orderId;

    @Field("order_date")
    private LocalDateTime orderDate;

    @Field("customer_id")
    private UUID customerId;

    @Field("total_amount")
    private BigDecimal totalAmount;

    @Field("order_status")
    private String orderStatus;

    @Field("warehouse_id")
    private UUID warehouseId;

    @Field("delivery_id")
    private UUID deliveryId;

    @Field("processing_time")
    private Integer processingTime;  // 처리 시간 (분)

    @Field("created_at")
    private LocalDateTime createdAt;

}
