package com.scm.common.user.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 요청 DTO
 * 
 * @author c.h.jo
 * @since 2026-01-28
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /**
     * 사용자명
     */
    @NotBlank(message = "사용자명은 필수입니다.")
    private String username;

    /**
     * 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
