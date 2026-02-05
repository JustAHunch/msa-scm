package com.logistics.scm.oms.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Inventory Service Application
 * 
 * 재고 관리 서비스
 * - 재고 가용성 조회
 * - 재고 예약/할당
 * - 안전재고 관리
 * - 멀티 창고 재고 관리
 * - 재고 동기화
 * - Outbox Pattern을 통한 이벤트 발행
 *
 * Port: 8082
 * Database: PostgreSQL (inventory_db, Port: 5433)
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@EnableScheduling
@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
