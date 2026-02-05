package com.scm.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * 
 * Spring Security 설정을 관리하는 클래스
 * 
 * 현재 구현:
 * - PasswordEncoder: BCrypt 사용 (비밀번호 암호화)
 * - AuthenticationManager: 로그인 인증 처리
 * - AuthenticationProvider: UserDetailsService와 연동
 * - JWT 기반 인증: Stateless 세션 관리
 * 
 * 향후 개선:
 * - JWT 인증 필터 추가
 * - 역할 기반 접근 제어 (RBAC)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * PasswordEncoder Bean 등록
     * BCrypt 알고리즘 사용 (강력한 암호화)
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager Bean 등록
     * 
     * 로그인 시 사용자 인증을 처리합니다.
     * 
     * @param config AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * AuthenticationProvider Bean 등록
     * 
     * UserDetailsService와 PasswordEncoder를 사용하여 인증을 처리합니다.
     * 
     * @return DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Security Filter Chain 설정
     * 
     * JWT 기반 인증을 위한 설정:
     * - CSRF 비활성화 (REST API는 CSRF 불필요)
     * - Stateless 세션 관리 (JWT 사용)
     * - 인증 API는 모두 허용
     * - 나머지는 인증 필요 (향후)
     * 
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (REST API는 CSRF 불필요)
                .csrf(AbstractHttpConfigurer::disable)
                
                // 세션 관리: Stateless (JWT 사용)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // 요청별 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 API는 모두 허용
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Actuator 헬스 체크 허용
                        .requestMatchers("/actuator/health").permitAll()
                        // 개발 단계: 모든 요청 허용 (향후 변경 예정)
                        .anyRequest().permitAll()
                );

        return http.build();
    }

}
