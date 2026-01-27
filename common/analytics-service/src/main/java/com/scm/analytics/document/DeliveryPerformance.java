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
 * DeliveryPerformance Document - 배송 성과 데이터
 * 
 * MongoDB Collection: delivery_performance
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "delivery_performance")
public class DeliveryPerformance {

    @Id
    private String id;

    @Field("delivery_id")
    private UUID deliveryId;

    @Field("driver_id")
    private UUID driverId;

    @Field("vehicle_id")
    private UUID vehicleId;

    @Field("delivery_date")
    private LocalDateTime deliveryDate;

    @Field("distance")
    private BigDecimal distance;  // km

    @Field("delivery_time")
    private Integer deliveryTime;  // 배송 시간 (분)

    @Field("on_time")
    private Boolean onTime;  // 정시 배송 여부

    @Field("created_at")
    private LocalDateTime createdAt;

}
