package com.scm.common.security.service;

import com.scm.common.user.entity.User;
import com.scm.common.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService 구현체
 * 
 * Spring Security가 사용자 인증 시 사용하는 서비스입니다.
 * 
 * @author c.h.jo
 * @since 2026-01-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자명으로 사용자 정보 조회
     * 
     * Spring Security가 로그인 시 호출합니다.
     * 
     * @param username 사용자명
     * @return UserDetails
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 비활성화된 사용자 체크
        if (!user.isEnabled()) {
            throw new RuntimeException("비활성화된 사용자입니다: " + username);
        }

        return buildUserDetails(user);
    }

    /**
     * User 엔티티를 Spring Security UserDetails로 변환
     * 
     * @param user User 엔티티
     * @return UserDetails
     */
    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(!user.isEnabled())
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }

    /**
     * 사용자 권한 정보 생성
     * 
     * @param user User 엔티티
     * @return 권한 목록
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }
}
