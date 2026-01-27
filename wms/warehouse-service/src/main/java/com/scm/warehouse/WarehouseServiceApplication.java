package com.scm.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Warehouse Service Application
 * 
 * WMS (Warehouse Management System)의 통합 서비스
 * 창고 관리, 입고, 피킹, 적치 기능을 모두 처리
 *
 * Port: 8084
 * Database: PostgreSQL (inventory_db, Port: 5434)
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class WarehouseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseServiceApplication.class, args);
    }
}
