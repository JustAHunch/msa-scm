package com.scm.common.security.web;

import com.scm.common.security.jwt.JwtProvider;
import com.scm.common.security.service.AuthService;
import com.scm.common.user.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 인증 REST API
 * 
 * JWT 토큰 발급 및 검증 API를 제공합니다.
 * 
 * Base URL: /api/auth
 * 
 * @author c.h.jo
 * @since 2025-01-28
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthResource {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    /**
     * 로그인 및 JWT 토큰 발급
     * 
     * POST /api/auth/login
     * 
     * @param loginRequest 로그인 요청 DTO
     * @return JWT 토큰 응답 DTO
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        
        try {
            JwtResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            throw new RuntimeException("로그인에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Access Token 갱신
     * 
     * POST /api/auth/refresh
     * 
     * @param refreshToken Refresh Token
     * @return 새로운 JWT 토큰 응답 DTO
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Token refresh requested");
        
        try {
            JwtResponseDTO response = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * JWT 토큰 검증
     * 
     * POST /api/auth/validate
     * 
     * API Gateway에서 호출하여 토큰의 유효성을 검증합니다.
     * 
     * @param request 토큰 검증 요청 DTO
     * @return 토큰 검증 응답 DTO
     */
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponseDTO> validateToken(
            @Valid @RequestBody TokenValidationRequestDTO request) {
        
        String token = request.getToken();
        log.debug("Token validation requested");

        try {
            // 토큰 유효성 검증
            boolean isValid = jwtProvider.validateToken(token);

            if (isValid) {
                // 토큰에서 사용자 정보 추출
                String username = jwtProvider.getUsernameFromToken(token);
                String authorities = jwtProvider.getAuthoritiesFromToken(token);
                Date expiration = jwtProvider.getExpirationFromToken(token);
                boolean isExpired = jwtProvider.isTokenExpired(token);

                TokenValidationResponseDTO response = TokenValidationResponseDTO.builder()
                        .valid(true)
                        .username(username)
                        .authorities(authorities)
                        .expiration(expiration)
                        .expired(isExpired)
                        .build();

                return ResponseEntity.ok(response);
            } else {
                // 유효하지 않은 토큰
                TokenValidationResponseDTO response = TokenValidationResponseDTO.builder()
                        .valid(false)
                        .errorMessage("Invalid token")
                        .build();

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            log.error("Token validation error", e);
            
            TokenValidationResponseDTO response = TokenValidationResponseDTO.builder()
                    .valid(false)
                    .errorMessage(e.getMessage())
                    .build();

            return ResponseEntity.ok(response);
        }
    }

    /**
     * 로그아웃
     * 
     * POST /api/auth/logout
     * 
     * JWT는 Stateless이므로 실제로는 클라이언트에서 토큰을 삭제하는 것으로 처리됩니다.
     * 서버에서는 로그 기록만 수행합니다.
     * 
     * 향후 개선:
     * - Redis를 사용한 토큰 블랙리스트 구현
     * - 토큰 만료 시간까지 블랙리스트에 보관
     * 
     * @return 로그아웃 성공 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        // Bearer 토큰에서 실제 토큰 추출
        String token = authHeader.substring(7);
        
        try {
            String username = jwtProvider.getUsernameFromToken(token);
            log.info("User logged out: {}", username);
            
            // TODO: Redis 블랙리스트에 토큰 추가 (향후 구현)
            
            return ResponseEntity.ok("로그아웃되었습니다.");
        } catch (Exception e) {
            log.error("Logout error", e);
            return ResponseEntity.ok("로그아웃되었습니다.");
        }
    }
}
