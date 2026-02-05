package com.scm.warehouse.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Auditor Aware Implementation
 * 
 * 현재 작업자 정보를 제공합니다.
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
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String USER_HEADER = "X-User-Name";
    private static final String DEFAULT_AUDITOR = "SYSTEM";

    @Override
    public Optional<String> getCurrentAuditor() {
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
    }
}
