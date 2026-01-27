# MSA-SCM (Supply Chain Management Platform)

물류 시스템을 위한 마이크로서비스 아키텍처 기반 공급망 관리 플랫폼

## 🏗️ 아키텍처 개요

이 프로젝트는 **옵션 A (실용적 접근)** 구조를 채택하여 OMS, WMS, TMS를 통합한 종합 물류 관리 시스템입니다. 6개 비즈니스 서비스로 통합 구성되어 관리 복잡도를 낮추고 실무 환경에 적합한 구조를 유지합니다.

상세한 아키텍처는 [Architecture Documentation](docs/ARCHITECTURE.md)을 참고하세요.

### 주요 도메인

- **OMS (Order Management System)**: 주문 + 재고 관리
- **WMS (Warehouse Management System)**: 창고 전체 통합 관리
- **TMS (Transportation Management System)**: 배송 전체 통합 관리
- **Common Services**: 공통 서비스 (사용자, 알림, 분석)

## 📦 모듈 구조

```
msa-scm/
├── infrastructure/          # 인프라 서비스
│   ├── eureka-server/      # 서비스 디스커버리 (8761)
│   ├── api-gateway/        # API 게이트웨이 (8080)
│   └── config-server/      # 설정 서버 (8888)
├── oms/                    # 주문 관리 시스템
│   ├── order-service/      # 주문 + 고객 통합 (8081)
│   └── inventory-service/  # 재고 관리 (8082)
├── wms/                    # 창고 관리 시스템
│   └── warehouse-service/  # 창고 전체 통합 (입고/피킹/패킹/작업자) (8084)
├── tms/                    # 운송 관리 시스템
│   └── delivery-service/   # 배송 전체 통합 (배송/차량/기사) (8087)
└── common/                 # 공통 서비스
    ├── common-service/     # 공통 도메인 (User/Code 등) (8090)
    ├── notification-service/ # 알림 서비스 (WebSocket/Email) (8091)
    └── analytics-service/  # 분석 서비스 (리포트/대시보드) (8092)
```

## 🛠️ 기술 스택

### Backend
- **Java**: 17
- **Spring Boot**: 3.2.1
- **Spring Cloud**: 2023.0.0
- **Build Tool**: Gradle 8.x

### Infrastructure
- **Service Discovery**: Eureka Server
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config
- **Message Broker**: Apache Kafka
- **Kafka Monitoring**: Kafka UI (포트: 8989)
- **Cache**: Redis

### Database
- **RDBMS**: PostgreSQL (Order, Inventory, Warehouse, Delivery, Common)
- **NoSQL**: MongoDB (Analytics Service)
- **Cache**: Redis (Notification, Session)

### Monitoring & Logging
- **Distributed Tracing**: Spring Cloud Sleuth + Zipkin
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana) - 예정

## 🚀 빠르게 시작하기

빠르게 시작하려면 [Quick Start Guide](QUICKSTART.md)를 참고하세요.

### 필수 요구사항

- JDK 17 이상
- Docker & Docker Compose
- Gradle 8.x

### 빌드 및 실행

```bash
# 전체 프로젝트 빌드
./gradlew clean build

# 특정 모듈 빌드
./gradlew :infrastructure:eureka-server:build

# Docker Compose로 인프라 서비스 실행
docker-compose up -d

# 각 마이크로서비스 개별 실행
cd infrastructure/eureka-server && ../../gradlew bootRun
cd infrastructure/api-gateway && ../../gradlew bootRun
cd common/common-service && ../../gradlew bootRun
cd oms/order-service && ../../gradlew bootRun
```

## 📊 주요 프로세스 플로우

### 1. 주문 접수 (OMS)
1. Order Service에서 주문 + 고객 정보 통합 관리
2. Inventory Service에서 재고 확인 및 예약
3. 주문 확정 이벤트 발행 (Kafka)

### 2. 창고 작업 (WMS)
1. Warehouse Service에서 입고/피킹/패킹 통합 처리
2. 창고 작업자 할당 및 작업 진행
3. 출고 완료 이벤트 발행

### 3. 배송 처리 (TMS)
1. Delivery Service에서 배송/차량/기사 통합 관리
2. 배송 계획 수립 및 차량/기사 배차
3. GPS 추적 및 배송 완료 처리

### 4. 고객 알림 (Common)
1. 주문 확인 알림 (WebSocket + Email)
2. 배송 시작 알림
3. 배송 완료 알림

## 🔗 외부 시스템 연동 (예정)

- **ERP System**: 회계/구매 연동
- **SCM System**: 공급망 관리, 재고 동기화
- **E-Commerce**: 온라인몰 주문 수신
- **Carrier API**: 택배사 송장 발행
- **GPS Tracking**: 차량 위치 추적
- **Payment Gateway**: 결제 처리 및 정산

## 📝 개발 가이드

상세한 개발 가이드는 `/docs` 디렉토리를 참고하세요.

- [시작 가이드](QUICKSTART.md) - 빠르게 시작하기
- [아키텍처 문서](docs/ARCHITECTURE.md) - 시스템 아키텍처 상세
- [코딩 컨벤션](docs/CODING_CONVENTION.md) - 개발 규칙
- [커밋 컨벤션](docs/COMMIT_CONVENTION.md) - 커밋 메시지 규칙
- [설정 가이드](docs/CONFIGURATION.md) - 환경 설정
- [프로젝트 진행 상황](docs/PROJECT_PROGRESS.md) - 개발 진행 현황

## 🎯 아키텍처 특징

- **6개 비즈니스 서비스**: 도메인별 통합 구조로 관리 복잡도 감소
- **Database per Service**: 각 서비스별 독립 데이터베이스
- **이벤트 기반 비동기 통신**: Kafka를 통한 서비스 간 결합도 최소화
- **실무 지향적 접근**: 실제 업무 환경에서 일반적인 서비스 분리 수준
- **향후 확장 가능**: 필요 시 서비스 세분화 가능한 구조

## 📄 라이센스

이 프로젝트는 학습 목적으로 제작되었습니다.

## 👥 기여

프로젝트 개선을 위한 제안이나 버그 리포트는 이슈로 등록해주세요.