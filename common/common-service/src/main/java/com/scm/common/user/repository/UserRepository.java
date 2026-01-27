package com.scm.common.user.repository;

import com.scm.common.user.entity.Role;
import com.scm.common.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Repository
 * 
 * User Entity에 대한 데이터베이스 접근을 담당하는 Repository
 * 
 * 제공 메서드:
 * - findByUsername: 사용자명으로 사용자 조회 (로그인용)
 * - findByEmail: 이메일로 사용자 조회
 * - findByRole: 역할별 사용자 목록 조회
 * - findByEnabled: 활성화 상태별 사용자 목록 조회
 * - existsByUsername: 사용자명 중복 체크
 * - existsByEmail: 이메일 중복 체크
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * 사용자명으로 사용자 조회
     * 로그인 시 사용
     * 
     * @param username 사용자명
     * @return Optional<User>
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회
     * 비밀번호 찾기 등에 사용
     * 
     * @param email 이메일
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * 역할별 사용자 목록 조회
     * 
     * @param role 사용자 역할
     * @return List<User>
     */
    List<User> findByRole(Role role);

    /**
     * 활성화 상태별 사용자 목록 조회
     * 
     * @param enabled 활성화 여부
     * @return List<User>
     */
    List<User> findByEnabled(boolean enabled);

    /**
     * 사용자명 중복 체크
     * 회원가입 시 사용
     * 
     * @param username 사용자명
     * @return boolean (존재하면 true)
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 중복 체크
     * 회원가입 시 사용
     * 
     * @param email 이메일
     * @return boolean (존재하면 true)
     */
    boolean existsByEmail(String email);

}
