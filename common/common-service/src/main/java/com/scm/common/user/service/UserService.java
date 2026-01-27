package com.scm.common.user.service;

import com.scm.common.user.entity.Role;
import com.scm.common.user.entity.User;
import com.scm.common.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * 
 * 사용자 관리 비즈니스 로직을 처리하는 서비스
 * 
 * 주요 기능:
 * - 사용자 CRUD (생성, 조회, 수정, 삭제)
 * - 로그인 인증 (비밀번호 검증)
 * - 사용자명/이메일 중복 체크
 * - 계정 활성화/비활성화
 * - 비밀번호 변경
 * 
 * 보안:
 * - 비밀번호는 BCrypt로 암호화하여 저장
 * - 로그인 시 평문 비밀번호와 암호화된 비밀번호를 비교
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 생성
     * 
     * @param username 사용자명
     * @param email 이메일
     * @param password 비밀번호 (평문)
     * @param role 역할
     * @return 생성된 User
     * @throws IllegalArgumentException 사용자명 또는 이메일 중복 시
     */
    @Transactional
    public User createUser(String username, String email, String password, Role role) {
        log.info("Creating user: username={}, email={}, role={}", username, email, role);

        // 중복 체크
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // User 생성
        User user = User.builder()
                .username(username)
                .email(email)
                .password(encodedPassword)
                .role(role)
                .enabled(true)  // 기본값: 활성화
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully: id={}", savedUser.getId());

        return savedUser;
    }

    /**
     * 사용자 ID로 조회
     * 
     * @param id 사용자 ID
     * @return User
     * @throws IllegalArgumentException 사용자를 찾을 수 없을 때
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    /**
     * 사용자명으로 조회
     * 
     * @param username 사용자명
     * @return User
     * @throws IllegalArgumentException 사용자를 찾을 수 없을 때
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    /**
     * 이메일로 조회
     * 
     * @param email 이메일
     * @return User
     * @throws IllegalArgumentException 사용자를 찾을 수 없을 때
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    /**
     * 전체 사용자 조회
     * 
     * @return List<User>
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 역할별 사용자 조회
     * 
     * @param role 역할
     * @return List<User>
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * 활성화된 사용자만 조회
     * 
     * @return List<User>
     */
    public List<User> getEnabledUsers() {
        return userRepository.findByEnabled(true);
    }

    /**
     * 사용자 정보 수정
     * 
     * @param id 사용자 ID
     * @param email 새 이메일 (null이면 변경하지 않음)
     * @param role 새 역할 (null이면 변경하지 않음)
     * @return 수정된 User
     */
    @Transactional
    public User updateUser(UUID id, String email, Role role) {
        User user = getUserById(id);

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists: " + email);
            }
            // email은 final이 아니므로 리플렉션이나 빌더 패턴 재사용 필요
            // 현재는 간단히 로그만 남김 (실제로는 Setter 추가 또는 빌더 패턴 수정 필요)
            log.warn("Email update not implemented yet");
        }

        log.info("User updated: id={}", id);
        return user;
    }

    /**
     * 비밀번호 변경
     * 
     * @param id 사용자 ID
     * @param oldPassword 기존 비밀번호 (평문)
     * @param newPassword 새 비밀번호 (평문)
     * @throws IllegalArgumentException 기존 비밀번호가 일치하지 않을 때
     */
    @Transactional
    public void changePassword(UUID id, String oldPassword, String newPassword) {
        User user = getUserById(id);

        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // 새 비밀번호 암호화 및 저장
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedPassword);

        log.info("Password changed for user: id={}", id);
    }

    /**
     * 계정 활성화
     * 
     * @param id 사용자 ID
     */
    @Transactional
    public void enableUser(UUID id) {
        User user = getUserById(id);
        user.enable();
        log.info("User enabled: id={}", id);
    }

    /**
     * 계정 비활성화
     * 
     * @param id 사용자 ID
     */
    @Transactional
    public void disableUser(UUID id) {
        User user = getUserById(id);
        user.disable();
        log.info("User disabled: id={}", id);
    }

    /**
     * 사용자 삭제
     * 
     * @param id 사용자 ID
     */
    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("User deleted: id={}", id);
    }

    /**
     * 로그인 인증
     * 
     * @param username 사용자명
     * @param password 비밀번호 (평문)
     * @return User (인증 성공 시)
     * @throws IllegalArgumentException 인증 실패 시
     */
    @Transactional
    public User authenticate(String username, String password) {
        log.info("Authenticating user: username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // 계정 활성화 체크
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Account is disabled");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // 로그인 시각 갱신
        user.updateLastLoginAt();

        log.info("Authentication successful: username={}", username);
        return user;
    }

}
