package com.scm.common.security.web;

import com.scm.common.security.jwt.JwtProvider;
import com.scm.common.security.service.AuthService;
import com.scm.common.security.service.TokenBlacklistService;
import com.scm.common.user.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * @since 2026-01-28
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 및 JWT 토큰 관리 API")
public class AuthResource {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 로그인 및 JWT 토큰 발급
     * 
     * POST /api/auth/login
     * 
     * @param loginRequest 로그인 요청 DTO
     * @return JWT 토큰 응답 DTO
     */
    @Operation(
            summary = "로그인",
            description = "사용자명과 비밀번호로 로그인하여 JWT Access Token과 Refresh Token을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = JwtResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 잘못된 사용자명 또는 비밀번호"
            )
    })
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
    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(implementation = JwtResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 Refresh Token"
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refreshToken(
            @Parameter(description = "Refresh Token", required = true)
            @RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Token refresh requested");
        
        try {
            if (tokenBlacklistService.isBlacklisted(refreshToken)) {
                log.warn("Blocked blacklisted token in validate endpoint");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

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
    @Operation(
            summary = "토큰 검증",
            description = "JWT 토큰의 유효성을 검증하고 사용자 정보를 반환합니다. API Gateway에서 내부적으로 호출합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 검증 완료 (유효/무효 여부는 응답 body의 valid 필드 참조)",
                    content = @Content(schema = @Schema(implementation = TokenValidationResponseDTO.class))
            )
    })
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponseDTO> validateToken(
            @Valid @RequestBody TokenValidationRequestDTO request) {
        
        String token = request.getToken();
        log.debug("Token validation requested");

        try {
            // 1. 블랙리스트 확인 (로그아웃된 토큰인지 체크)
            if (tokenBlacklistService.isBlacklisted(token)) {
                log.warn("Blocked blacklisted token in validate endpoint");
                TokenValidationResponseDTO response = TokenValidationResponseDTO.builder()
                        .valid(false)
                        .errorMessage("Token has been revoked")
                        .build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 2. 토큰 유효성 검증
            boolean isValid = jwtProvider.validateToken(token);

            if (!isValid) {
                // 유효하지 않은 토큰
                TokenValidationResponseDTO response = TokenValidationResponseDTO.builder()
                        .valid(false)
                        .errorMessage("Invalid token")
                        .build();

                return ResponseEntity.ok(response);
            }

            // 3. 토큰에서 사용자 정보 추출
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
     * Access Token과 Refresh Token을 모두 Redis 블랙리스트에 추가하여 무효화합니다.
     * 클라이언트는 토큰을 삭제해야 합니다.
     *
     * 동작 방식:
     * 1. Access Token과 Refresh Token을 Redis 블랙리스트에 추가 (TTL: 토큰 만료 시간까지)
     * 2. API Gateway에서 블랙리스트 확인 후 차단
     * 3. 클라이언트는 로컬 스토리지에서 토큰 삭제
     *
     * @return 로그아웃 성공 메시지
     */
    @Operation(
            summary = "로그아웃",
            description = "로그아웃 처리를 수행합니다. Access Token과 Refresh Token을 모두 Redis 블랙리스트에 추가하여 무효화합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 토큰"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Parameter(description = "Authorization 헤더 (Bearer {token})", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "Refresh Token (선택사항)")
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {

        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("유효하지 않은 Authorization 헤더입니다.");
        }

        // Bearer 토큰에서 실제 토큰 추출 (Access Token)
        String accessToken = authHeader.substring(7);

        try {
            // Access Token 유효성 검증
            if (!jwtProvider.validateToken(accessToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("유효하지 않은 토큰입니다.");
            }

            String username = jwtProvider.getUsernameFromToken(accessToken);

            // 1. Access Token을 Redis 블랙리스트에 추가
            boolean accessTokenAdded = tokenBlacklistService.addToBlacklist(accessToken);

            // 2. Refresh Token도 블랙리스트에 추가 (있는 경우)
            boolean refreshTokenAdded = true;
            if (refreshToken != null && !refreshToken.isEmpty()) {
                if (jwtProvider.validateToken(refreshToken)) {
                    refreshTokenAdded = tokenBlacklistService.addToBlacklist(refreshToken);
                    log.info("Refresh token added to blacklist for user: {}", username);
                }
            }

            if (accessTokenAdded) {
                log.info("User logged out successfully: {}", username);
                if (refreshTokenAdded) {
                    return ResponseEntity.ok("로그아웃되었습니다.");
                } else {
                    return ResponseEntity.ok("로그아웃되었습니다.");
                }
            } else {
                log.warn("Failed to add token to blacklist for user: {}", username);
                return ResponseEntity.ok("로그아웃되었습니다. (블랙리스트 등록 실패)");
            }
            
        } catch (Exception e) {
            log.error("Logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그아웃 처리 중 오류가 발생했습니다.");
        }
    }
}
