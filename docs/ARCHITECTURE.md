# MSA-SCM Architecture Documentation

## 시스템 개요

MSA-SCM은 물류 공급망 관리를 위한 마이크로서비스 아키텍처 기반 플랫폼입니다.

## 아키텍처 원칙

### 1. 도메인 주도 설계 (DDD)
- OMS, WMS, TMS를 독립적인 Bounded Context로 분리
- 각 도메인은 자체 데이터베이스 소유 (Database per Service)
- 도메인 간 통신은 이벤트 기반 비동기 방식

### 2. 마이크로서비스 패턴
- **Service Discovery**: Eureka를 통한 동적 서비스 검색
- **API Gateway**: 단일 진입점을 통한 라우팅 및 로드밸런싱
- **Circuit Breaker**: 장애 전파 방지
- **Config Server**: 중앙 집중식 설정 관리

### 3. 데이터 관리
- Database per Service 패턴
- CQRS (Command Query Responsibility Segregation)
- Event Sourcing (주문 이력 추적)
- Saga 패턴 (분산 트랜잭션)

## 계층 구조

```
┌─────────────────────────────────────────────────┐
│         Client Layer (Web/Mobile App)           │
└─────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────┐
│              API Gateway (8080)                  │
│  - Routing, Load Balancing                      │
│  - Authentication & Authorization                │
│  - Rate Limiting                                 │
└─────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────┐
│            Service Discovery (8761)              │
│              Eureka Server                       │
└─────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────┐
│              Business Services                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │   OMS    │  │   WMS    │  │   TMS    │      │
│  └──────────┘  └──────────┘  └──────────┘      │
└─────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────┐
│         Infrastructure Services                  │
│  - Message Broker (Kafka)              │
│  - Cache (Redis)                                │
│  - Monitoring (Prometheus, Grafana, Zipkin)     │
└─────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────┐
│              Data Layer                          │
│  - PostgreSQL (RDBMS)                           │
│  - MongoDB (NoSQL)                              │
└─────────────────────────────────────────────────┘
```

## 서비스 상세

### Infrastructure Services

#### 1. Eureka Server (8761)
**책임**:
- 서비스 등록 및 검색
- 헬스 체크
- 서비스 인스턴스 관리

**기술**:
- Spring Cloud Netflix Eureka Server

#### 2. API Gateway (8080)
**책임**:
- 모든 클라이언트 요청의 진입점
- 서비스별 라우팅
- 로드 밸런싱
- 인증/인가 처리
- Rate Limiting

**기술**:
- Spring Cloud Gateway
- Spring Security (인증/인가)

**라우팅 규칙**:
```
/common-service/api/v1/**          → common-service (8090)
/order-service/api/v1/**           → order-service (8081)
/inventory-service/api/v1/**       → inventory-service (8082)
/warehouse-service/api/v1/**       → warehouse-service (8084)
/delivery-service/api/v1/**        → delivery-service (8087)
/notification-service/api/v1/**    → notification-service (8091)
/analytics-service/api/v1/**       → analytics-service (8092)
```

#### 3. Config Server (8888)
**책임**:
- 중앙 집중식 설정 관리
- 환경별 설정 (dev, staging, prod)
- 동적 설정 변경
- 암호화된 속성 관리

**기술**:
- Spring Cloud Config
- Git 기반 설정 저장소 (선택적)

### OMS (Order Management System)

#### 1. Order Service (8081)
**책임**:
- 주문 접수 및 생성
- 주문 상태 관리 (접수 → 확인 → 출고 → 배송 → 완료)
- 주문 취소/변경
- 다채널 주문 통합 (온라인몰, 모바일 등)
- 고객 정보 관리
- 주문 이력 조회
- 배송지 관리
- CS 접수 및 처리
- 반품/교환 처리

**주요 엔티티**:
- Order: 주문 정보
- OrderItem: 주문 상품
- OrderStatus: 주문 상태
- Customer: 고객 정보
- Address: 배송지
- CustomerInquiry: 고객 문의

**이벤트**:
- OrderCreatedEvent
- OrderConfirmedEvent
- OrderCancelledEvent

#### 2. Inventory Service (8082)
**책임**:
- 재고 가용성 조회
- 재고 예약 및 할당
- 안전재고 관리
- 멀티 창고 재고 통합
- 재고 동기화

**주요 엔티티**:
- Inventory: 재고 정보
- StockMovement: 재고 이동 이력
- SafetyStock: 안전재고

**캐싱 전략**:
- Redis를 활용한 재고 조회 캐싱
- TTL: 10분
- Write-through 패턴

### WMS (Warehouse Management System)

#### 1. Warehouse Service (8084)
**책임**:
- 창고 정보 관리
- 구역/로케이션 관리
- 창고 작업자 관리
- 작업 할당 및 스케줄링
- 피킹 작업 관리
- 피킹 리스트 생성
- 피킹 경로 최적화
- 바코드/RFID 스캔 처리
- 패킹 작업 관리
- 입고 관리
- 입고 검수
- 적치 작업
- QC 검사
- 입고 실적 관리

**주요 엔티티**:
- Warehouse: 창고 정보
- Zone: 창고 구역
- Location: 로케이션
- Worker: 작업자
- PickingList: 피킹 리스트
- PickingItem: 피킹 아이템
- PackingBox: 패킹 박스
- InboundOrder: 입고 지시
- InboundItem: 입고 상품
- QualityCheck: 품질 검사

### TMS (Transportation Management System)

#### 1. Delivery Service (8087)
**책임**:
- 배송 계획 수립
- 배송 경로 최적화
- 배송 지시
- 배송 실적 관리
- 배송 추적
- 차량 정보 관리
- 차량 배차
- 차량 위치 추적 (GPS)
- 적재 최적화
- 차량 유지보수 관리
- 배송기사 정보 관리
- 근무 스케줄 관리
- 작업 할당
- 실적 관리
- 모바일 앱 연동

**주요 엔티티**:
- DeliveryOrder: 배송 지시
- DeliveryRoute: 배송 경로
- DeliveryStatus: 배송 상태
- Vehicle: 차량 정보
- VehicleLocation: 차량 위치
- VehicleMaintenance: 유지보수 이력
- Driver: 배송기사 정보
- DriverSchedule: 근무 스케줄
- DriverPerformance: 실적

### Common Services

#### 1. Notification Service (8091)
**책임**:
- 알림 발송 (SMS, Email, Push)
- 배송 상태 알림
- 주문 확인 알림
- 알림 템플릿 관리

**통신 채널**:
- SMS: Twilio, AWS SNS
- Email: SMTP, SendGrid
- Push: FCM, APNs

#### 2. Analytics Service (8092)
**책임**:
- 데이터 수집 및 분석
- 리포트 생성
- 대시보드 제공
- KPI 모니터링

**기술**:
- MongoDB (시계열 데이터)
- Elasticsearch (로그 분석)

## 데이터 흐름

### 주문 처리 프로세스

```
1. 주문 접수 (Order Service)
   ↓
2. 재고 확인 및 예약 (Inventory Service)
   ↓
3. 피킹 리스트 생성 및 작업 (Warehouse Service)
   ↓
4. 배송 계획 및 차량/기사 배차 (Delivery Service)
   ↓
5. 배송 추적 및 완료
   ↓
6. 고객 알림 (Notification Service)
```

## 통신 패턴

### 1. 동기 통신
- REST API (서비스 간 직접 호출)
- OpenFeign 사용

### 2. 비동기 통신
- Kafka를 통한 이벤트 발행/구독
- CQRS 패턴 적용

### 3. 이벤트 종류

**Order Events**:
- OrderCreatedEvent
- OrderConfirmedEvent
- OrderCancelledEvent

**Inventory Events**:
- InventoryReservedEvent
- InventoryReleasedEvent
- StockLowEvent

**Delivery Events**:
- DeliveryStartedEvent
- DeliveryCompletedEvent
- DeliveryFailedEvent

## 보안

### 인증/인가
- Spring Security + JWT
- OAuth 2.0 (선택적)

### API Gateway Security
- Rate Limiting
- CORS 설정
- API Key 관리

## 모니터링

### 분산 추적
- Spring Cloud Sleuth
- Zipkin

### 메트릭 수집
- Micrometer
- Prometheus
- Grafana

### 로깅
- Logback
- ELK Stack (선택적)

## 확장성

### 수평 확장
- 각 마이크로서비스는 독립적으로 확장 가능
- Load Balancer를 통한 트래픽 분산

### 데이터베이스 샤딩
- 주문 데이터: 날짜 기준 파티셔닝
- 고객 데이터: 지역 기준 샤딩

## 장애 복원력

### Circuit Breaker
- Resilience4j
- Fallback 메커니즘

### Retry Pattern
- 재시도 정책 설정
- Exponential Backoff

### Bulkhead Pattern
- 서비스별 스레드 풀 격리

## 배포 전략

### Blue-Green Deployment
- 무중단 배포
- 빠른 롤백

### Canary Deployment
- 점진적 트래픽 이동
- A/B 테스트

## 성능 최적화

### 캐싱 전략
- Redis: 재고 정보, 세션
- Local Cache: 정적 데이터

### 데이터베이스 최적화
- 인덱싱 전략
- 읽기 전용 복제본
- Connection Pooling

## 참고 자료

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Microservices Patterns](https://microservices.io/patterns)
- [Domain-Driven Design](https://domainlanguage.com/ddd/)
