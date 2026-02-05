package com.scm.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Common Service Application
 * 
 * 공통 서비스: User, Code, File 등 횡단 관심사(Cross-Cutting Concerns) 관리
 * 
 * 주요 책임:
 * - User: 사용자 인증/인가, 사용자 정보 관리
 * - Code: 공통 코드 관리 (향후 확장)
 * - File: 파일 업로드/다운로드 (향후 확장)
 * - Audit: 감사 로그 (향후 확장)
 * 
 * Port: 8090
 * Database: PostgreSQL (common_db, Port: 5436)
 *
 * @author c.h.jo
 * @since 2026-01-27
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class CommonServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonServiceApplication.class, args);
    }

}
