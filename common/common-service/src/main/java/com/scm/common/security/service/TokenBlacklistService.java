package com.scm.common.security.service;

import com.scm.common.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Token Blacklist Service
 * 
 * 로그아웃된 JWT 토큰을 Redis에 저장하여 블랙리스트로 관리합니다.
 * 
 * 주요 기능:
 * - 로그아웃 시 토큰을 블랙리스트에 추가
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

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;
    
    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * 토큰을 블랙리스트에 추가
     * 
     * @param token JWT 토큰
     * @return 성공 여부
     */
    public boolean addToBlacklist(String token) {
        try {
            // 토큰 만료 시간 확인
            Date expiration = jwtProvider.getExpirationFromToken(token);
            long now = System.currentTimeMillis();
            long ttl = expiration.getTime() - now;
            
            if (ttl <= 0) {
                log.warn("Token already expired, not adding to blacklist");
                return false;
            }
            
            // Redis에 저장 (만료 시간까지만)
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(
                key,
                "revoked",
                ttl,
                TimeUnit.MILLISECONDS
            );
            
            log.info("Token added to blacklist, TTL: {} seconds", ttl / 1000);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to add token to blacklist", e);
            // 블랙리스트 추가 실패해도 클라이언트는 토큰 삭제했으므로 일단 OK
            return false;
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * 
     * @param token JWT 토큰
     * @return 블랙리스트에 있으면 true
     */
    public boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check blacklist", e);
            // Redis 장애 시 false 반환 (통과)
            // 또는 true 반환 (차단) - 보안 정책에 따라 결정
            return false;
        }
    }
}
