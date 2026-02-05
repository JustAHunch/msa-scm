package com.scm.common.security.service;

import com.scm.common.security.jwt.JwtProperties;
import com.scm.common.security.jwt.JwtProvider;
import com.scm.common.user.web.dto.JwtResponseDTO;
import com.scm.common.user.web.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 인증 서비스
 * 
 * JWT 토큰 발급 및 인증 처리를 담당합니다.
 * 
 * @author c.h.jo
 * @since 2026-01-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    /**
     * 로그인 처리 및 JWT 토큰 발급
     * 
     * @param loginRequest 로그인 요청 DTO
     * @return JWT 토큰 응답 DTO
     */
    public JwtResponseDTO login(LoginRequestDTO loginRequest) {
        // 1. 사용자 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. JWT 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        // 3. 사용자 정보 추출
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        log.info("User logged in: {}, role: {}", userDetails.getUsername(), role);

        // 4. 응답 DTO 생성
        return JwtResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration() / 1000) // 초 단위로 변환
                .username(userDetails.getUsername())
                .role(role)
                .build();
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token 발급
     * 
     * @param refreshToken Refresh Token
     * @return JWT 토큰 응답 DTO
     */
    public JwtResponseDTO refreshAccessToken(String refreshToken) {
        // 1. Refresh Token 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        // 2. Refresh Token에서 사용자 정보 추출
        String username = jwtProvider.getUsernameFromToken(refreshToken);
        String authorities = jwtProvider.getAuthoritiesFromToken(refreshToken);

        // 3. 권한 정보를 포함한 Authentication 객체 생성
        // JWT 생성 시 authorities가 필요하므로, 토큰에서 추출한 권한 정보를 사용
        Collection<GrantedAuthority> grantedAuthorities = 
            Arrays.stream(authorities.split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        // UserDetails 객체 생성 (JWT 생성을 위해 필요)
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password("") // 비밀번호는 불필요 (이미 인증됨)
                .authorities(grantedAuthorities)
                .build();
        
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,  // Principal은 UserDetails 객체여야 함
                    null, 
                    grantedAuthorities
                );

        // 4. 새로운 Access Token 생성
        String newAccessToken = jwtProvider.generateAccessToken(authentication);

        log.info("Access token refreshed for user: {}", username);

        // 5. 응답 DTO 생성
        return JwtResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 기존 Refresh Token 재사용
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration() / 1000)
                .username(username)
                .role(authorities)
                .build();
    }
}
