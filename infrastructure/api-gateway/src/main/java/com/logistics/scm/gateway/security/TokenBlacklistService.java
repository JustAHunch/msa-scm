package com.logistics.scm.gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

/**
 * Token Blacklist Service
 * 
 * 로그아웃된 JWT 토큰을 Redis에 저장하여 블랙리스트로 관리합니다.
 * 
 * 주요 기능:
 * - 로그아웃 시 토큰을 블랙리스트에 추가
 * - API 요청 시 토큰이 블랙리스트에 있는지 확인
 * - TTL을 통해 만료된 토큰은 자동으로 삭제
 * 
 * Redis Key 형식:
 * - Key: "blacklist:{token}"
 * - Value: "revoked"
 * - TTL: 토큰의 남은 유효 시간
 * 
 * @author c.h.jo
 * @since 2025-02-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    
    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * 토큰을 블랙리스트에 추가
     * 
     * @param token JWT 토큰
     * @return 성공 여부
     */
    public Mono<Boolean> addToBlacklist(String token) {
        try {
            // 토큰 만료 시간 확인
            Date expiration = jwtTokenProvider.getExpiration(token);
            long now = System.currentTimeMillis();
            long ttl = expiration.getTime() - now;
            
            if (ttl <= 0) {
                log.warn("Token already expired, not adding to blacklist");
                return Mono.just(false);
            }
            
            // Redis에 저장 (만료 시간까지만)
            String key = BLACKLIST_PREFIX + token;
            Duration duration = Duration.ofMillis(ttl);
            
            return reactiveRedisTemplate.opsForValue()
                    .set(key, "revoked", duration)
                    .doOnSuccess(success -> {
                        if (Boolean.TRUE.equals(success)) {
                            log.info("Token added to blacklist, TTL: {} seconds", ttl / 1000);
                        } else {
                            log.error("Failed to add token to blacklist");
                        }
                    })
                    .doOnError(error -> 
                        log.error("Redis error while adding token to blacklist", error)
                    )
                    .onErrorReturn(false);
            
        } catch (Exception e) {
            log.error("Failed to add token to blacklist", e);
            return Mono.just(false);
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * 
     * @param token JWT 토큰
     * @return 블랙리스트에 있으면 true
     */
    public Mono<Boolean> isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            
            return reactiveRedisTemplate.hasKey(key)
                    .doOnNext(exists -> {
                        if (Boolean.TRUE.equals(exists)) {
                            log.debug("Token found in blacklist");
                        }
                    })
                    .doOnError(error -> 
                        log.error("Redis error while checking blacklist", error)
                    )
                    .onErrorReturn(false);  // Redis 장애 시 통과 (보안 정책에 따라 변경 가능)
            
        } catch (Exception e) {
            log.error("Failed to check blacklist", e);
            return Mono.just(false);
        }
    }
}
