# API Gateway 인증/인가 설정 가이드

API Gateway의 JWT 기반 인증/인가 시스템 설명서입니다.

## 📋 목차

1. [개요](#개요)
2. [아키텍처](#아키텍처)
3. [JWT 토큰 검증 흐름](#jwt-토큰-검증-흐름)
4. [구현 상세](#구현-상세)
5. [설정 방법](#설정-방법)
6. [인증 제외 경로](#인증-제외-경로)
7. [테스트 방법](#테스트-방법)
8. [트러블슈팅](#트러블슈팅)

## 개요

API Gateway는 모든 외부 요청의 진입점으로, JWT 기반 인증/인가를 수행합니다.

### 주요 기능
- ✅ JWT 토큰 검증
- ✅ 사용자 정보 추출 및 전달 (X-User-Name, X-User-Role 헤더)
- ✅ 인증 제외 경로 관리
- ✅ 에러 응답 처리

### 기술 스택
- **Spring Cloud Gateway**: Reactive 기반 API Gateway
- **JJWT 0.12.3**: JWT 토큰 파싱 및 검증
- **Common Service**: JWT 토큰 발급 서비스

## 아키텍처

```
┌──────────┐      ┌──────────────┐      ┌─────────────┐
│  Client  │─────►│ API Gateway  │─────►│  Service    │
│          │      │              │      │ (Order/     │
│          │      │ JWT Filter   │      │  Inventory/ │
│          │      │ (검증)       │      │  etc.)      │
└──────────┘      └──────────────┘      └─────────────┘
                        │
                        ▼
                  ┌──────────────┐
                  │ Common       │
                  │ Service      │
                  │ (JWT 발급)   │
                  └──────────────┘
```

### 요청 흐름

1. **Client** → API Gateway에 JWT 토큰과 함께 요청
2. **JwtAuthenticationFilter** → 토큰 검증
3. **성공 시** → 사용자 정보를 헤더에 추가하여 Downstream 서비스로 전달
4. **실패 시** → 401 Unauthorized 응답

## JWT 토큰 검증 흐름

```
Client Request
  │
  ▼
Authorization Header 존재? ───No──► 401 Unauthorized
  │ Yes
  ▼
Bearer Token 형식? ───No──► 401 Unauthorized
  │ Yes
  ▼
Token 추출
  │
  ▼
Token 유효성 검증 ───Invalid──► 401 Unauthorized
  │ Valid
  ▼
사용자 정보 추출
  │
  ▼
X-User-Name, X-User-Role 헤더 추가
  │
  ▼
Downstream Service 전달
```

## 구현 상세

### 1. JwtTokenProvider
JWT 토큰 파싱 및 검증을 담당합니다.

**위치**: `infrastructure/api-gateway/src/main/java/com/logistics/scm/gateway/security/JwtTokenProvider.java`

**주요 메서드**:
```java
// 토큰 유효성 검증
public boolean validateToken(String token)

// 사용자명 추출
public String getUsername(String token)

// 역할 추출
public String getRole(String token)
```

**특징**:
- HMAC-SHA256 알고리즘 사용
- Common Service와 **동일한 Secret Key** 사용 필수
- 토큰 만료, 서명 오류, 형식 오류 등 모든 예외 처리

### 2. JwtAuthenticationFilter
Spring Cloud Gateway의 GatewayFilter로 구현된 인증 필터입니다.

**위치**: `infrastructure/api-gateway/src/main/java/com/logistics/scm/gateway/filter/JwtAuthenticationFilter.java`

**처리 로직**:
1. 인증 제외 경로 확인
2. Authorization 헤더 추출 및 검증
3. Bearer 토큰 추출
4. 토큰 유효성 검증
5. 사용자 정보 추출 및 헤더 추가

**전달되는 헤더**:
- `X-User-Name`: 사용자 이름 (JWT의 subject)
- `X-User-Role`: 사용자 역할 (ADMIN, MANAGER, OPERATOR, CUSTOMER)

### 3. 에러 응답 형식
```json
{
  "error": "Invalid or expired JWT token",
  "status": 401
}
```

## 설정 방법

### 1. JWT Secret Key 설정

**개발 환경** (application.yml):
```yaml
jwt:
  secret: scm-jwt-secret-key-minimum-256-bits-required-for-hs256-algorithm-this-is-example-key
```

**운영 환경** (환경 변수):
```bash
# Docker Compose
environment:
  - JWT_SECRET=your-production-secret-key-here

# Kubernetes
env:
  - name: JWT_SECRET
    valueFrom:
      secretKeyRef:
        name: jwt-secret
        key: secret-key
```

⚠️ **주의**: Common Service와 API Gateway는 **동일한 Secret Key**를 사용해야 합니다!

### 2. 필터 적용 설정

**전역 적용** (모든 라우트):
```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - JwtAuthenticationFilter
```

**특정 라우트만 적용**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/order-service/**
          filters:
            - StripPrefix=1
            - JwtAuthenticationFilter  # 이 라우트에만 적용
```

## 인증 제외 경로

다음 경로들은 JWT 인증을 거치지 않습니다:

| 경로 패턴 | 설명 | 예시 |
|----------|------|------|
| `/actuator/health` | 헬스 체크 | `GET /actuator/health` |
| `/swagger-ui/**` | Swagger UI | `GET /swagger-ui/index.html` |
| `/v3/api-docs/**` | OpenAPI 문서 | `GET /v3/api-docs` |
| `/api-docs/**` | API 문서 | `GET /order-service/api-docs` |
| `/eureka/**` | Eureka 관련 | `GET /eureka` |
| `/api/auth/**` | 인증 API | `POST /api/auth/login` |

**코드 위치**: `JwtAuthenticationFilter.isExcludedPath()`

### 제외 경로 추가 방법

```java
private boolean isExcludedPath(String path) {
    return path.contains("/actuator/health") ||
           path.contains("/swagger-ui") ||
           path.contains("/v3/api-docs") ||
           path.contains("/api-docs") ||
           path.startsWith("/eureka") ||
           path.contains("/api/auth/") ||
           path.contains("/public/");  // 추가 예시
}
```

## 테스트 방법

### 1. Common Service에서 JWT 토큰 발급

```bash
# 로그인 (JWT 토큰 발급)
curl -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# 응답 예시
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

### 2. API Gateway를 통한 요청 (토큰 없음)

```bash
# 인증 없이 요청 → 401 Unauthorized
curl -X GET http://localhost:8080/order-service/api/orders
```

**응답**:
```json
{
  "error": "Missing or invalid Authorization header",
  "status": 401
}
```

### 3. API Gateway를 통한 요청 (토큰 포함)

```bash
# JWT 토큰과 함께 요청 → 200 OK
curl -X GET http://localhost:8080/order-service/api/orders \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**성공 시**: Order Service가 응답 반환

### 4. 인증 제외 경로 테스트

```bash
# 토큰 없이 헬스 체크 → 200 OK
curl -X GET http://localhost:8080/order-service/actuator/health

# 토큰 없이 Swagger UI → 200 OK
curl -X GET http://localhost:8080/order-service/swagger-ui/index.html
```

### 5. Postman 테스트

**Dev-Gateway Environment 사용**:
1. Common Service에서 로그인하여 JWT 토큰 발급
2. `jwtToken` 환경 변수에 토큰 저장
3. Authorization 탭에서 Type: `Bearer Token`, Token: `{{jwtToken}}`
4. 요청 전송

## 트러블슈팅

### 1. Secret Key 불일치
**증상**: 항상 "Invalid JWT token" 에러
```
ERROR: Invalid JWT signature
```

**해결**:
```bash
# Common Service와 API Gateway의 Secret Key가 동일한지 확인
# Common Service application.yml
jwt:
  secret: scm-jwt-secret-key...

# API Gateway application.yml
jwt:
  secret: scm-jwt-secret-key...  # 동일해야 함!
```

### 2. 토큰 만료
**증상**: "Expired JWT token" 에러
```json
{
  "error": "Invalid or expired JWT token",
  "status": 401
}
```

**해결**: 새로운 토큰 발급

```bash
# 재로그인
curl -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### 3. Bearer 접두사 누락
**증상**: "Missing or invalid Authorization header" 에러

**잘못된 예**:
```
Authorization: eyJhbGciOiJIUzI1NiJ9...
```

**올바른 예**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 4. 인증 제외 경로가 작동하지 않음
**확인 사항**:
1. 경로 패턴이 `isExcludedPath()` 메서드에 정의되어 있는지
2. 로그 레벨을 DEBUG로 설정하여 확인
```yaml
logging:
  level:
    com.logistics.scm.gateway: DEBUG
```

**로그 예시**:
```
DEBUG: Skipping JWT validation for excluded path: /actuator/health
```

### 5. Downstream 서비스에서 사용자 정보 접근

Downstream 서비스(Order Service 등)에서 사용자 정보를 추출하려면:

```java
@RestController
public class OrderResource {
    
    @GetMapping("/api/orders")
    public ResponseEntity<?> getOrders(
            @RequestHeader("X-User-Name") String username,
            @RequestHeader("X-User-Role") String role) {
        
        log.info("User: {}, Role: {}", username, role);
        // 비즈니스 로직
    }
}
```

## 보안 권장 사항

### 1. Secret Key 관리
- ✅ 최소 256비트 (32자 이상) 사용
- ✅ 운영 환경에서는 반드시 환경 변수로 설정
- ✅ Git에 커밋하지 않기 (.gitignore 추가)
- ✅ 정기적으로 키 교체 (Key Rotation)

### 2. 토큰 만료 시간
- ✅ Access Token: 15분 ~ 1시간 (짧게)
- ✅ Refresh Token: 7일 ~ 30일 (길게)

### 3. HTTPS 사용
- ✅ 운영 환경에서는 반드시 HTTPS 사용
- ✅ 토큰이 평문으로 전송되지 않도록 주의

### 4. Rate Limiting
API Gateway에 Rate Limiting 추가 권장:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

## 다음 단계

1. [ ] Common Service에 JWT 발급 API 구현
2. [ ] Refresh Token 메커니즘 추가
3. [ ] Role 기반 권한 체크 (RBAC)
4. [ ] API Gateway에 Rate Limiting 추가
5. [ ] Circuit Breaker 패턴 적용

## 참고 자료

- [Spring Cloud Gateway Documentation](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [JWT.io](https://jwt.io/) - JWT 디버깅 도구

---

**마지막 업데이트**: 2025-01-28
**버전**: 1.0.0
**작성자**: c.h.jo
