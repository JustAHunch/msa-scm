package com.scm.common.user.web.resource;

import com.scm.common.user.entity.Role;
import com.scm.common.user.entity.User;
import com.scm.common.user.service.UserService;
import com.scm.common.user.web.dto.UserRequestDTO;
import com.scm.common.user.web.dto.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User REST API Resource
 * 
 * 사용자 관리 REST API 엔드포인트
 * 
 * Base URL: /api/v1/users
 * 
 * 엔드포인트:
 * - POST   /api/v1/users              : 사용자 생성
 * - GET    /api/v1/users/{id}         : 사용자 조회
 * - GET    /api/v1/users              : 전체 사용자 조회
 * - GET    /api/v1/users/role/{role}  : 역할별 사용자 조회
 * - PUT    /api/v1/users/{id}         : 사용자 수정
 * - DELETE /api/v1/users/{id}         : 사용자 삭제
 * - POST   /api/v1/users/login        : 로그인
 * - POST   /api/v1/users/{id}/password: 비밀번호 변경
 * - POST   /api/v1/users/{id}/enable  : 계정 활성화
 * - POST   /api/v1/users/{id}/disable : 계정 비활성화
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserResource {

    private final UserService userService;

    /**
     * 사용자 생성
     * 
     * POST /api/v1/users
     * 
     * @param request 사용자 생성 요청
     * @return 201 Created + 생성된 사용자 정보
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO.Create request) {
        
        log.info("Creating user: username={}", request.getUsername());
        
        User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponseDTO.from(user));
    }

    /**
     * 사용자 ID로 조회
     * 
     * GET /api/v1/users/{id}
     * 
     * @param id 사용자 ID
     * @return 200 OK + 사용자 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        log.info("Getting user by id: {}", id);
        
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    /**
     * 전체 사용자 조회
     * 
     * GET /api/v1/users
     * 
     * @return 200 OK + 사용자 목록
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("Getting all users");
        
        List<UserResponseDTO> users = userService.getAllUsers().stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(users);
    }

    /**
     * 역할별 사용자 조회
     * 
     * GET /api/v1/users/role/{role}
     * 
     * @param role 역할 (ADMIN, MANAGER, OPERATOR, CUSTOMER)
     * @return 200 OK + 사용자 목록
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable Role role) {
        log.info("Getting users by role: {}", role);
        
        List<UserResponseDTO> users = userService.getUsersByRole(role).stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(users);
    }

    /**
     * 사용자 정보 수정
     * 
     * PUT /api/v1/users/{id}
     * 
     * @param id 사용자 ID
     * @param request 사용자 수정 요청
     * @return 200 OK + 수정된 사용자 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequestDTO.Update request) {
        
        log.info("Updating user: id={}", id);
        
        User user = userService.updateUser(id, request.getEmail(), request.getRole());
        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    /**
     * 사용자 삭제
     * 
     * DELETE /api/v1/users/{id}
     * 
     * @param id 사용자 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Deleting user: id={}", id);
        
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 비밀번호 변경
     * 
     * POST /api/v1/users/{id}/password
     * 
     * @param id 사용자 ID
     * @param request 비밀번호 변경 요청
     * @return 200 OK
     */
    @PostMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequestDTO.ChangePassword request) {
        
        log.info("Changing password for user: id={}", id);
        
        userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * 계정 활성화
     * 
     * POST /api/v1/users/{id}/enable
     * 
     * @param id 사용자 ID
     * @return 200 OK
     */
    @PostMapping("/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable UUID id) {
        log.info("Enabling user: id={}", id);
        
        userService.enableUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 계정 비활성화
     * 
     * POST /api/v1/users/{id}/disable
     * 
     * @param id 사용자 ID
     * @return 200 OK
     */
    @PostMapping("/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable UUID id) {
        log.info("Disabling user: id={}", id);
        
        userService.disableUser(id);
        return ResponseEntity.ok().build();
    }

}
