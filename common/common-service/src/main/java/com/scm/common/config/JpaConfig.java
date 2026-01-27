package com.scm.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA Configuration
 * 
 * JPA Repository 스캔 범위 및 기본 설정을 관리하는 설정 클래스
 * 
 * basePackages:
 * - com.scm.common.user.repository: User 도메인 Repository
 * - com.scm.common.code.repository: Code 도메인 Repository (향후 추가)
 * - com.scm.common.file.repository: File 도메인 Repository (향후 추가)
 */
@Configuration
@EnableJpaRepositories(
    basePackages = {
        "com.scm.common.user.repository"
        // 향후 추가: "com.scm.common.code.repository",
        // 향후 추가: "com.scm.common.file.repository"
    }
)
public class JpaConfig {
    // JPA 관련 추가 설정이 필요한 경우 이곳에 Bean 정의
}
