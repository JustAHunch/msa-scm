package com.logistics.scm.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server Application
 * 
 * 마이크로서비스들의 설정을 중앙에서 관리하는 Config Server
 * - 환경별 설정 관리 (dev, staging, prod)
 * - Git 기반 설정 저장소
 * - 동적 설정 변경
 *
 * Port: 8888
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
