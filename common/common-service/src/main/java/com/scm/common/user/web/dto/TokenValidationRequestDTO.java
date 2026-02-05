package com.scm.common.user.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 토큰 검증 요청 DTO
 * 
 * @author c.h.jo
 * @since 2026-01-28
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationRequestDTO {

    /**
     * 검증할 JWT 토큰
     */
    @NotBlank(message = "토큰은 필수입니다.")
    private String token;
}
