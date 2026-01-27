package com.logistics.scm.oms.inventory.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Auditor Aware Implementation
 * 
 * 현재 작업자 정보를 제공합니다.
 * - 현재는 "SYSTEM"을 반환
 * - 향후 Spring Security Context에서 실제 사용자 정보 추출 예정
 * - API Gateway에서 전달된 X-User-Id 헤더 활용 가능
 * 
 * TODO: 인증 시스템 구축 시 실제 사용자 정보로 변경
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // TODO: Spring Security Context 또는 ThreadLocal에서 실제 사용자 ID 추출
        return Optional.of("SYSTEM");
    }
}
