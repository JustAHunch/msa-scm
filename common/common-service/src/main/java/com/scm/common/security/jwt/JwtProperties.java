package com.scm.common.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 Properties
 * 
 * application.yml의 jwt.* 속성을 바인딩합니다.
 * 
 * @author c.h.jo
 * @since 2025-01-28
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명에 사용되는 비밀 키
     * 
     * 주의사항:
     * - 운영 환경에서는 반드시 환경 변수나 외부 설정으로 관리
     * - 최소 256비트(32자) 이상의 강력한 키 사용 권장
     * - 정기적인 키 로테이션 필요
     */
    private String secret;

    /**
     * Access Token 만료 시간 (밀리초)
     * 기본값: 86400000 (24시간)
     */
    private Long expiration;

    /**
     * Refresh Token 만료 시간 (밀리초)
     * 기본값: 604800000 (7일)
     */
    private Long refreshExpiration;
}
