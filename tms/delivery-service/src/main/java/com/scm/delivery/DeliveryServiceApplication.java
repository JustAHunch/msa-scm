package com.scm.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Delivery Service Application
 * 
 * TMS (Transportation Management System)의 통합 서비스
 * 배송 관리, 차량 관리, 배송기사 관리 기능을 모두 처리
 *
 * Port: 8087
 * Database: PostgreSQL (order_db, Port: 5435)
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class DeliveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }
}
