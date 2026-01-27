package com.scm.common.user.entity;

import com.scm.common.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Entity
 * 
 * 시스템 사용자 정보를 관리하는 Entity
 * 
 * 주요 책임:
 * - 사용자 기본 정보 관리 (username, email, password)
 * - 역할(Role) 관리: ADMIN, MANAGER, OPERATOR, CUSTOMER
 * - 계정 상태 관리 (enabled: 활성/비활성)
 * - 로그인 이력 추적 (lastLoginAt)
 * 
 * 보안:
 * - password는 BCrypt로 암호화하여 저장
 * - username과 email은 unique 제약 조건
 * 
 * 향후 확장:
 * - 프로필 정보 (이름, 전화번호, 부서 등)
 * - 권한(Permission) 세분화
 * - 2FA (Two-Factor Authentication)
 * - 비밀번호 변경 이력
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    /**
     * 사용자 ID (Primary Key)
     * UUID를 사용하여 분산 환경에서 중복 없이 ID 생성
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID id;

    /**
     * 사용자명 (로그인 ID)
     * 유니크 제약 조건, 중복 불가
     */
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /**
     * 이메일
     * 유니크 제약 조건, 중복 불가
     * 비밀번호 찾기, 알림 발송 등에 사용
     */
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    /**
     * 비밀번호 (BCrypt 암호화)
     * 평문으로 저장하지 않고 BCrypt로 해시화하여 저장
     * 예: BCryptPasswordEncoder.encode("password123")
     */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    /**
     * 사용자 역할
     * ADMIN, MANAGER, OPERATOR, CUSTOMER 중 하나
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    /**
     * 계정 활성화 여부
     * true: 활성화 (로그인 가능)
     * false: 비활성화 (로그인 불가)
     */
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    /**
     * 마지막 로그인 일시
     * 로그인 성공 시 갱신됨
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 비즈니스 로직: 로그인 성공 시 호출
     * lastLoginAt을 현재 시각으로 갱신
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 비즈니스 로직: 계정 활성화
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * 비즈니스 로직: 계정 비활성화
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * 비즈니스 로직: 비밀번호 변경
     * @param encodedPassword BCrypt로 암호화된 비밀번호
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}
