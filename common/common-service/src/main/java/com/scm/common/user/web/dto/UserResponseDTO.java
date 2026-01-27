package com.scm.common.user.web.dto;

import com.scm.common.user.entity.Role;
import com.scm.common.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Response DTO
 * 
 * 사용자 정보 응답 시 사용하는 DTO
 * 
 * 보안:
 * - password는 절대 응답에 포함하지 않음
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private UUID id;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    /**
     * Entity -> DTO 변환
     * 
     * @param user User Entity
     * @return UserResponseDTO
     */
    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .lastModifiedAt(user.getLastModifiedAt())
                .build();
    }

}
