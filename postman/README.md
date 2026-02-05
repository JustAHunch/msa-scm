# Postman Collections & Environments

SCM 플랫폼의 API 엔드포인트 테스트를 위한 Postman Collection과 Environment 파일입니다.

## 📁 디렉토리 구조

```
postman/
├── collections/
│   └── Order-Service-API.postman_collection.json
├── environments/
│   ├── Local.postman_environment.json
│   └── Dev-Gateway.postman_environment.json
└── README.md (현재 파일)
```

## 🚀 빠른 시작

### 1. Postman에 Collection 가져오기

1. Postman 실행
2. **Import** 버튼 클릭
3. `postman/collections/Order-Service-API.postman_collection.json` 파일 선택
4. Import 완료

### 2. Environment 설정

1. Postman의 **Environments** 탭으로 이동
2. **Import** 버튼 클릭
3. `postman/environments/` 폴더의 환경 파일 선택
4. 사용할 환경 선택 (Local 또는 Dev-Gateway)

### 3. 테스트 실행

1. Collection에서 원하는 요청 선택
2. **Send** 버튼 클릭
3. Response 확인

## 📝 Collection 구성

### Order Service API

#### 1. Health Check
- **Service Health**: 서비스 상태 확인

#### 2. Order Management
- **Create Order**: 새로운 주문 생성
- **Get Order by ID**: 주문 ID로 조회
- **Get All Orders**: 전체 주문 목록 조회 (페이징)
- **Update Order**: 주문 정보 수정
- **Delete Order**: 주문 삭제

#### 3. Order Status Management
- **Confirm Order**: 주문 확정 (PENDING → CONFIRMED)
- **Ship Order**: 주문 출고 (CONFIRMED → SHIPPED)
- **Deliver Order**: 배송 완료 (SHIPPED → DELIVERED)
- **Cancel Order**: 주문 취소 (ANY → CANCELLED)

## 🌍 Environment 설정

### Local Environment
Order Service에 직접 접근하는 환경입니다.

| 변수 | 값 | 설명 |
|------|-----|------|
| `baseUrl` | `http://localhost:8081` | Order Service 직접 URL |
| `customerId` | `550e8400-e29b-41d4-a716-446655440001` | 테스트용 고객 ID |
| `orderId` | (자동 설정) | 생성된 주문 ID |

**사용 시나리오**:
- Order Service 단독 테스트
- API Gateway 우회 필요 시
- 빠른 개발 및 디버깅

### Dev-Gateway Environment
API Gateway를 경유하는 환경입니다.

| 변수 | 값 | 설명 |
|------|-----|------|
| `baseUrl` | `http://localhost:8080/order-service` | Gateway 경유 URL |
| `gatewayUrl` | `http://localhost:8080` | API Gateway URL |
| `eurekaUrl` | `http://localhost:8761` | Eureka Server URL |
| `customerId` | `550e8400-e29b-41d4-a716-446655440001` | 테스트용 고객 ID |
| `orderId` | (자동 설정) | 생성된 주문 ID |
| `jwtToken` | (수동 설정) | JWT 인증 토큰 |

**사용 시나리오**:
- 프로덕션과 유사한 환경 테스트
- API Gateway 라우팅 검증
- JWT 인증/인가 테스트

## 🔑 자동화된 테스트 스크립트

### Pre-request Script
모든 요청 전에 실행됩니다.
```javascript
console.log('Request to: ' + pm.request.url);
```

### Test Script
모든 요청 후에 실행됩니다.
```javascript
// 응답 시간 검증
pm.test("Response time is less than 3000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(3000);
});
```

### 주문 생성 후 자동 변수 설정
```javascript
// Create Order 요청의 Test Script
var jsonData = pm.response.json();
pm.environment.set("orderId", jsonData.orderId);
```

## 📊 테스트 시나리오 예시

### 시나리오 1: 전체 주문 프로세스
1. **Health Check** - 서비스 상태 확인
2. **Create Order** - 새 주문 생성 (orderId 자동 저장)
3. **Get Order by ID** - 생성된 주문 조회
4. **Confirm Order** - 주문 확정
5. **Ship Order** - 주문 출고
6. **Deliver Order** - 배송 완료

### 시나리오 2: 주문 취소
1. **Create Order** - 새 주문 생성
2. **Get Order by ID** - 주문 확인
3. **Cancel Order** - 주문 취소

### 시나리오 3: 주문 수정
1. **Create Order** - 새 주문 생성
2. **Update Order** - 주문 수량/상품 변경
3. **Get Order by ID** - 변경 사항 확인

## 🔧 고급 기능

### Collection Runner
여러 요청을 순차적으로 실행합니다.

1. Collection 우클릭 → **Run collection**
2. 실행할 요청 선택
3. Environment 선택
4. **Run** 버튼 클릭

### Newman (CLI 실행)
```bash
# Newman 설치
npm install -g newman

# Collection 실행
newman run postman/collections/Order-Service-API.postman_collection.json \
  -e postman/environments/Local.postman_environment.json

# HTML 리포트 생성
newman run postman/collections/Order-Service-API.postman_collection.json \
  -e postman/environments/Local.postman_environment.json \
  -r html --reporter-html-export report.html
```

## 🐛 트러블슈팅

### 연결 오류
```
Error: connect ECONNREFUSED 127.0.0.1:8081
```
**해결**: Order Service가 실행 중인지 확인
```bash
# Docker Compose 실행
docker-compose up -d

# Service 로그 확인
docker-compose logs order-service
```

### JWT 토큰 만료
```
401 Unauthorized
```
**해결**: 새로운 JWT 토큰 발급 후 환경 변수에 설정
1. Common Service에서 `/api/v1/auth/login` 호출
2. 반환된 토큰을 `jwtToken` 변수에 저장

### orderId가 설정되지 않음
**해결**: Create Order 요청의 Test Script 확인
```javascript
pm.environment.set("orderId", jsonData.orderId);
```

## 📚 추가 리소스

- [Swagger UI](http://localhost:8081/swagger-ui/index.html): Order Service API 문서
- [Eureka Dashboard](http://localhost:8761): 서비스 등록 상태 확인
- [API Gateway](http://localhost:8080): Gateway 라우팅 정보

## 🤝 기여 가이드

새로운 API 엔드포인트가 추가되면:

1. Collection에 해당 요청 추가
2. Test Script 작성 (상태 코드, 응답 검증)
3. README.md 업데이트

## 📄 라이선스

이 프로젝트는 Apache 2.0 라이선스를 따릅니다.

---

**마지막 업데이트**: 2026-01-28
**버전**: 1.0.0
