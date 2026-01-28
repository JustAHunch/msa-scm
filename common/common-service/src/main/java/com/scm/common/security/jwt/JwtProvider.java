package com.scm.common.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증 Provider
 * 
 * 주요 기능:
 * - Access Token 생성
 * - Refresh Token 생성
 * - 토큰 검증
 * - 토큰에서 사용자 정보 추출
 * 
 * @author c.h.jo
 * @since 2025-01-28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    /**
     * Secret Key 생성
     * HMAC-SHA 알고리즘을 사용하여 서명 키 생성
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     * 
     * @param authentication Spring Security Authentication 객체
     * @return JWT Access Token
     */
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getExpiration());
    }

    /**
     * Refresh Token 생성
     * 
     * @param authentication Spring Security Authentication 객체
     * @return JWT Refresh Token
     */
    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getRefreshExpiration());
    }

    /**
     * JWT 토큰 생성 (공통 로직)
     * 
     * @param authentication Spring Security Authentication 객체
     * @param expirationTime 만료 시간 (밀리초)
     * @return JWT Token
     */
    private String generateToken(Authentication authentication, Long expirationTime) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // 권한 정보 추출
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 토큰에서 사용자명 추출
     * 
     * @param token JWT Token
     * @return 사용자명
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * 토큰에서 권한 정보 추출
     * 
     * @param token JWT Token
     * @return 권한 정보 (comma-separated)
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("authorities", String.class);
    }

    /**
     * 토큰 유효성 검증
     * 
     * @param token JWT Token
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 토큰 만료 시간 추출
     * 
     * @param token JWT Token
     * @return 만료 시간
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }

    /**
     * 토큰이 만료되었는지 확인
     * 
     * @param token JWT Token
     * @return 만료되었으면 true, 그렇지 않으면 false
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
