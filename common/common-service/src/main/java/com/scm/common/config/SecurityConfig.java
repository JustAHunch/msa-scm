package com.scm.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
 * - 모든 엔드포인트 허용 (개발 단계)
 * 
 * 향후 개선:
 * - JWT 인증 필터 추가
 * - 로그인/회원가입 엔드포인트만 허용
 * - 나머지 엔드포인트는 JWT 검증 필요
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
     * Security Filter Chain 설정
     * 
     * 현재는 개발 단계이므로 모든 요청 허용
     * 향후 JWT 인증 필터 추가 예정
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
                
                // 모든 요청 허용 (개발 단계)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

}
