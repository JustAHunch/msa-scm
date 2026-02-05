package com.scm.common.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JWT 토큰 응답 DTO
 * 
 * @author c.h.jo
 * @since 2026-01-28
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {

    /**
     * Access Token
     */
    private String accessToken;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * 토큰 타입 (Bearer)
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access Token 만료 시간 (초)
     */
    private Long expiresIn;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 사용자 역할
     */
    private String role;
}
