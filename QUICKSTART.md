# MSA-SCM Quick Start

빠르게 프로젝트를 시작하기 위한 간단한 가이드입니다.

## 📋 사전 준비

1. **JDK 17** 설치 확인
   ```bash
   java -version
   ```

2. **Docker Desktop** 실행
   ```bash
   docker --version
   docker-compose --version
   ```

## 🚀 빠르게 시작하기

### 1단계: 인프라 서비스 시작 (Docker)

```bash
# 프로젝트 루트 디렉토리에서
docker-compose up -d

# 실행 확인
docker ps
```

다음 서비스들이 실행됩니다:
- PostgreSQL (포트: 5432-5436)
- Redis (포트: 6379)
- MongoDB (포트: 27017)
- Kafka + Zookeeper (포트: 9092, 2181)
- Kafka UI (포트: 8989)
- Zipkin (포트: 9411)
- Prometheus (포트: 9090)
- Grafana (포트: 3001)

### 2단계: Eureka Server 실행

새 터미널을 열고:

```bash
cd infrastructure/eureka-server
../../gradlew bootRun
```

브라우저에서 확인: http://localhost:8761

### 3단계: API Gateway 실행

또 다른 터미널에서:

```bash
cd infrastructure/api-gateway
../../gradlew bootRun
```

### 4단계: Common Service 실행 (권장)

Common Service는 사용자 인증/인가의 기반이 되므로 먼저 실행하는 것을 권장합니다.

```bash
cd common/common-service
../../gradlew bootRun
```

### 5단계: Order Service 실행

```bash
cd oms/order-service
../../gradlew bootRun
```

### 6단계: 확인

Eureka 대시보드에서 서비스 등록 확인:
- http://localhost:8761

## 🔍 주요 URL

| 서비스 | URL                   | 용도 |
|--------|-----------------------|------|
| Eureka Server | http://localhost:8761 | 서비스 등록 현황 |
| API Gateway | http://localhost:8080 | API 진입점 |
| Kafka UI | http://localhost:8989 | Kafka 토픽 모니터링 |
| Zipkin | http://localhost:9411 | 분산 추적 |
| Prometheus | http://localhost:9090 | 메트릭 수집 |
| Grafana | http://localhost:3001 | 모니터링 대시보드 (admin/admin) |

## 🧪 API 테스트

### Common Service - User API
```bash
# 사용자 생성
curl -X POST http://localhost:8080/api/common-service/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "password123",
    "role": "ADMIN"
  }'

# 사용자 목록 조회
curl http://localhost:8080/api/common-service/v1/users
```

### Order Service API
```bash
# 주문 생성
curl -X POST http://localhost:8080/api/order-service/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-uuid",
    "items": [
      {
        "productCode": "PROD-001",
        "quantity": 2,
        "unitPrice": 10000
      }
    ]
  }'
```

## 📚 다음 단계

1. [Getting Started Guide](docs/GETTING_STARTED.md) - 상세한 시작 가이드
2. [Architecture Documentation](docs/ARCHITECTURE.md) - 아키텍처 이해
3. [Coding Convention](docs/CODING_CONVENTION.md) - 개발 규칙
4. [Commit Convention](docs/COMMIT_CONVENTION.md) - 커밋 메시지 규칙

## ❓ 문제 해결

### 포트 충돌
```bash
# Windows에서 포트 사용 확인
netstat -ano | findstr :8761

# 프로세스 종료 후 재시작
```

### Docker 서비스 재시작
```bash
docker-compose down
docker-compose up -d
```

### Gradle 빌드 문제
```bash
./gradlew clean build --refresh-dependencies
```

### Kafka UI 접속 안 됨
```bash
# Kafka 컨테이너 로그 확인
docker logs kafka

# Kafka UI 컨테이너 재시작
docker restart kafka-ui
```

## 📞 도움이 필요하신가요?

- 이슈 등록: GitHub Issues
- 문서 확인: `/docs` 디렉토리
- 진행 상황: [PROJECT_PROGRESS.md](docs/PROJECT_PROGRESS.md)