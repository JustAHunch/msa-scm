# Getting Started Guide

MSA-SCM 프로젝트를 시작하기 위한 가이드입니다.

## 사전 준비사항

### 필수 설치 항목

1. **JDK 17 이상**
   ```bash
   java -version
   # Java version "17" 이상이어야 함
   ```

2. **Docker & Docker Compose**
   ```bash
   docker --version
   docker-compose --version
   ```

3. **Gradle 8.x** (또는 Wrapper 사용)
   ```bash
   ./gradlew --version
   ```

## 프로젝트 구조

```
msa-scm/
├── infrastructure/          # 인프라 서비스
│   ├── eureka-server/      # Service Discovery (8761)
│   ├── api-gateway/        # API Gateway (8080)
│   └── config-server/      # Config Server (8888)
├── oms/                    # Order Management System
│   ├── order-service/      # 주문 + 고객 (8081)
│   └── inventory-service/  # 재고 (8082)
├── wms/                    # Warehouse Management System
│   └── warehouse-service/  # 창고 전체 (8084)
├── tms/                    # Transportation Management System
│   └── delivery-service/   # 배송 전체 (8087)
├── common/                 # Common Services
│   ├── common-service/     # 공통 (8090)
│   ├── notification-service/ # 알림 (8091)
│   └── analytics-service/  # 분석 (8092)
└── docker-compose.yml
```

## 실행 방법

### 1. 인프라 서비스 시작

먼저 Docker Compose로 데이터베이스와 메시지 브로커를 실행합니다.

```bash
# 모든 인프라 서비스 시작
docker-compose up -d

# 주요 서비스만 시작
docker-compose up -d postgres-order postgres-inventory postgres-warehouse postgres-delivery redis mongodb rabbitmq
```

### 2. 서비스 실행 순서

마이크로서비스는 다음 순서로 실행하는 것을 권장합니다:

#### Step 1: Eureka Server 시작
```bash
cd infrastructure/eureka-server
../../gradlew bootRun
```

브라우저에서 확인: http://localhost:8761

#### Step 2: Config Server 시작
```bash
cd infrastructure/config-server
../../gradlew bootRun
```

#### Step 3: API Gateway 시작
```bash
cd infrastructure/api-gateway
../../gradlew bootRun
```

#### Step 4: 비즈니스 서비스 시작

```bash
# Order Service (주문 + 고객)
cd oms/order-service
../../gradlew bootRun

# Inventory Service
cd oms/inventory-service
../../gradlew bootRun

# Warehouse Service (창고 전체)
cd wms/warehouse-service
../../gradlew bootRun

# Delivery Service (배송 전체)
cd tms/delivery-service
../../gradlew bootRun

# Notification Service
cd common/notification-service
../../gradlew bootRun

# Analytics Service
cd common/analytics-service
../../gradlew bootRun
```

### 3. 전체 빌드

```bash
# 루트 디렉토리에서
./gradlew clean build

# 테스트 제외하고 빌드
./gradlew clean build -x test
```

## 서비스 확인

### Eureka Dashboard
- URL: http://localhost:8761
- 등록된 모든 서비스를 확인할 수 있습니다.

### API Gateway
- URL: http://localhost:8080
- 모든 서비스의 진입점입니다.

### 개별 서비스 Health Check

```bash
# Order Service
curl http://localhost:8081/actuator/health

# Inventory Service
curl http://localhost:8082/actuator/health

# Warehouse Service
curl http://localhost:8084/actuator/health

# Delivery Service
curl http://localhost:8087/actuator/health

# Common Service
curl http://localhost:8090/actuator/health

# Notification Service
curl http://localhost:8091/actuator/health

# Analytics Service
curl http://localhost:8092/actuator/health
```

## API 테스트

### API Gateway를 통한 요청

```bash
# Order Service (via Gateway)
curl http://localhost:8080/api/order-service/v1/orders

# Customer API (via Gateway - Order Service)
curl http://localhost:8080/api/order-service/v1/customers

# Inventory Service (via Gateway)
curl http://localhost:8080/api/inventory-service/v1/inventories

# Warehouse Service (via Gateway)
curl http://localhost:8080/api/warehouse-service/v1/warehouses

# Delivery Service (via Gateway)
curl http://localhost:8080/api/delivery-service/v1/deliveries
```

### 직접 서비스 호출

```bash
# Order Service 직접 호출
curl http://localhost:8081/api/v1/orders

# Inventory Service 직접 호출
curl http://localhost:8082/api/v1/inventories

# Warehouse Service 직접 호출
curl http://localhost:8084/api/v1/warehouses

# Delivery Service 직접 호출
curl http://localhost:8087/api/v1/deliveries
```

## 모니터링 도구

### Zipkin (분산 추적)
- URL: http://localhost:9411
- 서비스 간 요청 흐름을 추적할 수 있습니다.

### Prometheus (메트릭 수집)
- URL: http://localhost:9090
- 서비스 메트릭을 수집하고 조회할 수 있습니다.

### Grafana (대시보드)
- URL: http://localhost:3001
- ID/PW: admin/admin
- 시각화된 모니터링 대시보드를 제공합니다.

## 데이터베이스 접속 정보

### PostgreSQL

각 서비스별로 독립된 데이터베이스를 사용합니다:

```
Order DB:
- Host: localhost:5432
- Database: order_db
- Username: postgres
- Password: postgres

Inventory DB:
- Host: localhost:5433
- Database: inventory_db
- Username: postgres
- Password: postgres

Warehouse DB:
- Host: localhost:5434
- Database: warehouse_db
- Username: postgres
- Password: postgres

Delivery DB:
- Host: localhost:5435
- Database: delivery_db
- Username: postgres
- Password: postgres

Notification DB (Redis):
- Host: localhost:6379
- No password (개발 환경)
```

### MongoDB (Analytics Service)

```
- Host: localhost:27017
- Database: analytics_db
- Username: admin
- Password: admin
```

### Redis

```
- Host: localhost:6379
- No password (개발 환경)
```

## 트러블슈팅

### 포트 충돌 발생 시

```bash
# 포트 사용 확인 (Windows)
netstat -ano | findstr :8761

# 포트 사용 확인 (Linux/Mac)
lsof -i :8761

# 프로세스 종료 후 재시작
```

### Eureka에 서비스가 등록되지 않을 때

1. Eureka Server가 먼저 실행되었는지 확인
2. application.yml의 eureka.client.service-url 확인
3. 네트워크 연결 확인

### 데이터베이스 연결 실패 시

1. Docker 컨테이너가 실행 중인지 확인:
   ```bash
   docker ps
   ```

2. 데이터베이스 로그 확인:
   ```bash
   docker logs postgres-order
   ```

3. 포트 중복 확인

## 다음 단계

- [코딩 컨벤션](CODING_CONVENTION.md)
- [커밋 컨벤션](COMMIT_CONVENTION.md)
- [API 문서](API_DOCUMENTATION.md)
- [배포 가이드](DEPLOYMENT.md)
