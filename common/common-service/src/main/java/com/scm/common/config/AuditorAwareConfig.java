package com.scm.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Auditor Aware Configuration
 * 
 * JPA Auditing에서 사용할 현재 사용자(Auditor) 정보를 제공하는 설정 클래스
 * 
 * 현재 구현:
 * - 모든 Audit 정보에 "SYSTEM"을 반환
 * 
 * 향후 개선 방안:
 * 1. Spring Security Context 사용:
 *    SecurityContextHolder.getContext().getAuthentication().getName()
 * 
 * 2. API Gateway 헤더 사용:
 *    HTTP 헤더에서 "X-User-Id" 추출하여 반환
 * 
 * 3. JWT 토큰 파싱:
 *    JWT에서 사용자 ID를 추출하여 반환
 */
@Configuration
public class AuditorAwareConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // TODO: Spring Security Context 또는 API Gateway 헤더에서 현재 사용자 ID 가져오기
            // 현재는 임시로 "SYSTEM" 반환
            return Optional.of("SYSTEM");
        };
    }

}
