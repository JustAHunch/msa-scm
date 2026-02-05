package com.scm.common.config;

import com.scm.common.user.entity.Role;
import com.scm.common.user.entity.User;
import com.scm.common.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 초기 데이터 로더
 * 
 * 애플리케이션 시작 시 테스트용 사용자 계정을 생성합니다.
 * 
 * @author c.h.jo
 * @since 2026-01-28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing test user data...");

        // ADMIN 사용자 생성
        createUserIfNotExists(
                "admin",
                "admin@scm.com",
                "admin123",
                Role.ADMIN
        );

        // MANAGER 사용자 생성
        createUserIfNotExists(
                "manager",
                "manager@scm.com",
                "manager123",
                Role.MANAGER
        );

        // OPERATOR 사용자 생성
        createUserIfNotExists(
                "operator",
                "operator@scm.com",
                "operator123",
                Role.OPERATOR
        );

        // CUSTOMER 사용자 생성
        createUserIfNotExists(
                "customer",
                "customer@scm.com",
                "customer123",
                Role.CUSTOMER
        );

        log.info("Test user data initialization completed.");
    }

    private void createUserIfNotExists(String username, String email, String password, Role role) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        
        if (existingUser.isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .enabled(true)
                    .build();

            userRepository.save(user);
            log.info("Created test user: {} ({})", username, role);
        } else {
            log.debug("User already exists: {}", username);
        }
    }
}
