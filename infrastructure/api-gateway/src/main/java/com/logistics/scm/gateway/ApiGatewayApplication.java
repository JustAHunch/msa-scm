package com.logistics.scm.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 * 
 * 모든 마이크로서비스의 진입점 역할을 하는 API Gateway
 * - 라우팅
 * - 로드 밸런싱
 * - 인증/인가
 * - Rate Limiting
 * - Circuit Breaker
 *
 * Port: 8080
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
