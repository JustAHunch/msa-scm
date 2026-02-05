package com.scm.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Notification Service Application
 * 
 * 알림 서비스
 * WebSocket을 통한 브라우저 실시간 알림 및 SMTP를 통한 이메일 발송 기능 제공
 *
 * Port: 8091
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
