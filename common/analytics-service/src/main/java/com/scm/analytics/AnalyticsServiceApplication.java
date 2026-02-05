package com.scm.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Analytics Service Application
 * 
 * 분석 및 리포팅 서비스
 * MongoDB를 활용한 시계열 데이터 수집, 분석, 리포트 생성 및 대시보드 제공
 *
 * Port: 8092
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AnalyticsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }
}
