package com.scm.common.user.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 토큰 검증 응답 DTO
 * 
 * @author c.h.jo
  * @since 2026-01-28
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponseDTO {

    /**
     * 토큰 유효 여부
     */
    private Boolean valid;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 권한 정보
     */
    private String authorities;

    /**
     * 토큰 만료 시간
     */
    private Date expiration;

    /**
     * 토큰이 만료되었는지 여부
     */
    private Boolean expired;

    /**
     * 에러 메시지 (유효하지 않은 경우)
     */
    private String errorMessage;
}
