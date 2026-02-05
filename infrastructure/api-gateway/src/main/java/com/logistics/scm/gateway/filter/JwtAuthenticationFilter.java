package com.logistics.scm.gateway.filter;

import com.logistics.scm.gateway.security.JwtTokenProvider;
import com.logistics.scm.gateway.security.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * JWT 인증 필터
 * 
 * API Gateway를 통과하는 모든 요청에서 JWT 토큰을 검증합니다.
 * 
 * 인증 제외 경로:
 * - /actuator/health (헬스 체크)
 * - /swagger-ui/** (Swagger UI)
 * - /v3/api-docs/** (OpenAPI Docs)
 * - /api/auth/** (인증 API)
 * 
 * 블랙리스트 체크:
 * - 로그아웃된 토큰은 Redis 블랙리스트에서 확인하여 차단
 * 
 * @author c.h.jo
 * @since 2025-01-28
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, 
                                   TokenBlacklistService tokenBlacklistService) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // 인증 제외 경로 체크
            if (isExcludedPath(path)) {
                log.debug("Skipping JWT validation for excluded path: {}", path);
                return chain.filter(exchange);
            }

            // Authorization 헤더 추출
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
                log.warn("Missing or invalid Authorization header for path: {}", path);
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            // Bearer 접두사 제거
            String token = authHeader.substring(BEARER_PREFIX.length());

            // 1. 토큰 검증
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("Invalid JWT token for path: {}", path);
                return onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
            }

            // 2. 블랙리스트 확인 (Reactive)
            return tokenBlacklistService.isBlacklisted(token)
                    .flatMap(isBlacklisted -> {
                        if (Boolean.TRUE.equals(isBlacklisted)) {
                            log.warn("Blocked blacklisted token for path: {}", path);
                            return onError(exchange, "Token has been revoked", HttpStatus.UNAUTHORIZED);
                        }

                        // 3. 사용자 정보 추출 및 헤더에 추가
                        String username = jwtTokenProvider.getUsername(token);
                        String role = jwtTokenProvider.getRole(token);

                        log.debug("JWT validation successful - username: {}, role: {}, path: {}", 
                                 username, role, path);

                        // 사용자 정보를 헤더에 추가하여 downstream 서비스에 전달
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Name", username)
                                .header("X-User-Role", role)
                                .build();

                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    });
        };
    }

    /**
     * 인증 제외 경로 확인
     */
    private boolean isExcludedPath(String path) {
        return path.contains("/actuator/health") ||
               path.contains("/swagger-ui") ||
               path.contains("/v3/api-docs") ||
               path.contains("/api-docs") ||
               path.startsWith("/eureka") ||
               path.contains("/api/auth/");
    }

    /**
     * 인증 실패 시 에러 응답
     */
    private Mono<Void> onError(org.springframework.web.server.ServerWebExchange exchange, 
                               String errorMessage, 
                               HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        
        String errorJson = String.format("{\"error\": \"%s\", \"status\": %d}", 
                                        errorMessage, httpStatus.value());
        
        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap(errorJson.getBytes())));
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}
