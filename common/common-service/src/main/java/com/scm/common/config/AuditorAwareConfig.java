package com.scm.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Auditor Aware Configuration
 * 
 * JPA Auditing에서 사용할 현재 사용자(Auditor) 정보를 제공하는 설정 클래스
 * 
 * 현재 구현:
 * - API Gateway에서 전달된 X-User-Name 헤더에서 사용자명 추출
 * - 헤더가 없는 경우 "SYSTEM" 반환
 * 
 * API Gateway JWT 필터에서 설정하는 헤더:
 * - X-User-Name: 사용자명
 * - X-User-Role: 사용자 권한
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Configuration
public class AuditorAwareConfig {

    private static final String USER_HEADER = "X-User-Name";
    private static final String DEFAULT_AUDITOR = "SYSTEM";

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                ServletRequestAttributes attributes = 
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String username = request.getHeader(USER_HEADER);
                    
                    if (username != null && !username.isEmpty()) {
                        return Optional.of(username);
                    }
                }
            } catch (Exception e) {
                // RequestContext가 없는 경우 (비동기 작업, 스케줄러 등)
                // SYSTEM으로 처리
            }
            
            return Optional.of(DEFAULT_AUDITOR);
        };
    }
}
