package com.logistics.scm.oms.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Order Service Application
 * 
 * 주문 관리 서비스
 * - 주문 접수 및 관리
 * - 주문 상태 추적
 * - 주문 취소/변경
 * - 배송지 관리
 * - 다채널 주문 통합
 * - Outbox Pattern을 통한 이벤트 발행
 *
 * Port: 8081
 * Database: PostgreSQL (order_db, Port: 5432)
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
