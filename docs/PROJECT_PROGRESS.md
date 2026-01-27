# Project Progress

MSA-SCM 프로젝트 진행 상황 추적 문서입니다.

## 프로젝트 개요

- **프로젝트명**: MSA-SCM (Supply Chain Management Platform)
- **시작일**: 2025-01-27
- **목표**: 물류 공급망 관리를 위한 마이크로서비스 플랫폼 구축
- **현재 버전**: 0.0.1-SNAPSHOT
- **아키텍처**: 옵션 A (실용적 접근 - 6개 비즈니스 서비스)

## 진행 상황 요약

| 단계 | 상태 | 완료일 |
|-----|------|--------|
| 프로젝트 초기 설정 | ✅ 완료 | 2025-01-27 |
| Infrastructure 서비스 구축 | ✅ 완료 | 2025-01-27 |
| OMS 기본 구조 구축 | ✅ 완료 | 2025-01-27 |
| WMS 기본 구조 구축 | ✅ 완료 | 2025-01-27 |
| TMS 기본 구조 구축 | ✅ 완료 | 2025-01-27 |
| Common Services 구축 | ✅ 완료 | 2025-01-27 |
| Entity 구현 | ✅ 완료 | 2025-01-27 |
| 비즈니스 로직 구현 | 📅 예정 | - |
| 테스트 코드 작성 | 📅 예정 | - |
| Docker 통합 환경 구축 | 📅 예정 | - |
| 모니터링 시스템 구축 | 📅 예정 | - |

## 시스템 구성 (옵션 A: 실용적 접근)

### 총 6개 비즈니스 서비스 + 3개 인프라 서비스

#### OMS (Order Management System)
1. ✅ **order-service** (8081) - 주문 + 고객 통합
2. ⏳ **inventory-service** (8082) - 재고

#### WMS (Warehouse Management System)
3. ⏳ **warehouse-service** (8084) - 창고 전체 (입고/피킹/적치 통합)

#### TMS (Transportation Management System)
4. ⏳ **delivery-service** (8087) - 배송 전체 (차량/기사/배송 통합)

#### Common Services
5. ✅ **common-service** (8090) - 공통 (User, Code 등 향후 확장)
6. ⏳ **notification-service** (8091) - 알림
7. ⏳ **analytics-service** (8092) - 분석

#### Infrastructure Services
- ✅ **eureka-server** (8761) - Service Discovery
- ✅ **api-gateway** (8080) - API Gateway
- ⏳ **config-server** (8888) - Config Server (선택적)

## Phase 1: 프로젝트 초기 설정 ✅

### 완료 항목
- [x] Gradle 멀티 모듈 프로젝트 구조 생성
- [x] settings.gradle 설정
- [x] 루트 build.gradle 설정
- [x] .gitignore 설정
- [x] README.md 작성

### 산출물
- `settings.gradle`: 9개 모듈 정의 (3개 인프라 + 6개 비즈니스)
- `build.gradle`: 공통 의존성 및 플러그인 설정
- `README.md`: 프로젝트 개요 및 구조 설명

## Phase 2: Infrastructure Services ✅

### 완료 항목

#### Eureka Server (8761)
- [x] 프로젝트 구조 생성
- [x] 의존성 설정
- [x] Application 클래스 작성
- [x] application.yml 설정
- [ ] 테스트 코드 작성

#### API Gateway (8080)
- [x] 프로젝트 구조 생성
- [x] 의존성 설정
- [x] Application 클래스 작성
- [x] 라우팅 규칙 설정 (OMS, WMS, TMS, Common)
- [ ] 인증/인가 설정
- [ ] Rate Limiting 설정
- [ ] Circuit Breaker 설정
- [ ] 테스트 코드 작성

#### Config Server (8888)
- [x] 프로젝트 구조 생성
- [x] 의존성 설정
- [x] Application 클래스 작성
- [x] Native 설정 적용
- [ ] Git 저장소 연동
- [ ] 암호화 설정
- [ ] 테스트 코드 작성

## Phase 3: OMS (Order Management System) 🔄

### Order Service (8081) - 주문 + 고객 통합
- [x] 프로젝트 구조 생성
- [x] 의존성 설정 (Web, JPA, PostgreSQL, Eureka)
- [x] Application 클래스 작성
- [x] application.yml 설정
- [x] Entity 정의 (Order, OrderItem, Customer, Address)
- [x] Repository 구현
- [x] Service 레이어 구현
- [x] REST API 구현 (OrderResource, CustomerResource)
- [ ] DTO 정의
- [ ] 비즈니스 로직 확장 (주문 상태 관리, CS 처리)
- [ ] 테스트 코드 작성

### Inventory Service (8082)
- [x] 프로젝트 구조 생성
- [x] 의존성 설정 (Redis 캐싱 포함)
- [x] Application 클래스 작성
- [x] application.yml 설정 (Redis 설정 포함)
- [x] BaseEntity 및 AuditorAware 구현
- [x] Entity 정의 (Inventory, StockMovement)
- [ ] Repository 구현
- [ ] Service 레이어 구현 (캐싱 적용)
- [ ] REST API 구현
- [ ] 동시성 제어 구현
- [ ] 테스트 코드 작성

## Phase 4: WMS (Warehouse Management System) ✅

### Warehouse Service (8084) - 창고 전체 통합
**통합 책임**:
- 창고 정보 관리 (구역/로케이션)
- 입고 관리 (검수, 적치, QC)
- 피킹 작업 관리 (리스트 생성, 경로 최적화, 패킹)
- 창고 작업자 및 작업 할당

**진행 상황**:
- [x] 프로젝트 구조 생성
- [x] 의존성 설정
- [x] Application 클래스 작성
- [x] application.yml 설정
- [x] BaseEntity 및 AuditorAware 구현
- [x] Entity 정의 완료 (8개)
  - Warehouse, Zone, Location
  - Worker
  - InboundOrder, InboundItem
  - PickingList, PickingItem
- [ ] Repository 구현
- [ ] Service 레이어 구현
- [ ] REST API 구현
- [ ] 테스트 코드 작성

## Phase 5: TMS (Transportation Management System) ✅

### Delivery Service (8087) - 배송 전체 통합
**통합 책임**:
- 배송 관리 (계획, 경로 최적화, 추적)
- 차량 관리 (배차, 위치 추적, 유지보수)
- 배송기사 관리 (스케줄, 작업 할당, 실적)

**진행 상황**:
- [x] 프로젝트 구조 생성
- [x] 의존성 설정
- [x] Application 클래스 작성
- [x] application.yml 설정
- [x] BaseEntity 및 AuditorAware 구현
- [x] Entity 정의 완료 (5개)
  - DeliveryOrder, DeliveryRoute
  - Vehicle, VehicleLocation
  - Driver
- [ ] Repository 구현
- [ ] Service 레이어 구현
- [ ] REST API 구현
- [ ] 테스트 코드 작성

## Phase 6: Common Services 🔄

### Common Service (8092) - 공통 도메인 관리 ✅
**통합 책임**:
- User: 사용자 인증/인가, 사용자 정보 관리
- Code: 공통 코드 관리 (향후 확장)
- File: 파일 업로드/다운로드 (향후 확장)
- Audit: 감사 로그 (향후 확장)

**진행 상황**:
- [x] 프로젝트 구조 생성
- [x] 의존성 설정 (Web, JPA, PostgreSQL, Eureka, Security)
- [x] Application 클래스 작성
- [x] application.yml 설정
- [x] BaseEntity 및 AuditorAware 구현
- [x] User Entity 및 Role Enum 구현
- [x] UserRepository 구현
- [x] UserService 구현 (인증, CRUD, 비밀번호 관리)
- [x] UserResource (REST API) 구현
- [x] SecurityConfig 구현 (PasswordEncoder)
- [x] Docker Compose에 common-db 추가 (Port: 5436)
- [x] API Gateway 라우팅 설정 (/api/users/v1/**)
- [ ] JWT 토큰 발급 기능
- [ ] JWT 검증 API
- [ ] 테스트 코드 작성

### Notification Service (8091)
- [x] 프로젝트 구조 생성
- [x] 의존성 설정 (Redis, WebSocket, Mail)
- [x] Application 클래스 작성
- [x] application.yml 설정
- [x] BaseEntity 및 AuditorAware 구현
- [x] Entity 정의 완료 (2개)
  - Notification
  - NotificationTemplate
- [ ] Repository 구현
- [ ] WebSocket 구현 (브라우저 알림)
- [ ] SMTP 이메일 발송 구현
- [ ] 알림 템플릿 관리
- [ ] 테스트 코드 작성

### Analytics Service (8092)
- [x] 프로젝트 구조 생성
- [x] MongoDB 의존성 설정
- [x] Application 클래스 작성
- [x] application.yml 설정 (MongoDB 연동)
- [x] MongoDB Document 정의 완료 (3개)
  - OrderAnalytics
  - DeliveryPerformance
  - WarehouseOperations
- [ ] Repository 구현
- [ ] 데이터 수집 로직 구현
- [ ] 리포트 생성 기능 구현
- [ ] 대시보드 API 구현
- [ ] 테스트 코드 작성

## Phase 7: Infrastructure 📅

### Message Broker
- [x] Docker Compose에 Kafka 설정
- [x] Kafka UI 설정 (Port: 8989) - 토픽/메시지 모니터링
- [ ] 이벤트 발행/구독 구현
- [ ] Dead Letter Queue 설정

### Database
- [x] PostgreSQL 컨테이너 설정
  - Order DB (5432)
  - Inventory DB (5433)
  - Warehouse DB (5434)
  - Delivery DB (5435)
- [x] MongoDB 컨테이너 설정 (Analytics)
- [x] Redis 컨테이너 설정 (Notification, Cache)
- [ ] 초기 스키마 생성
- [ ] 샘플 데이터 로드

### Monitoring
- [x] Zipkin 컨테이너 설정 (Port: 9411)
- [x] Prometheus 컨테이너 설정 (Port: 9090)
- [x] Grafana 컨테이너 설정 (Port: 3001)
- [x] Kafka UI 컨테이너 설정 (Port: 8989)
- [ ] 메트릭 수집 설정
- [ ] 대시보드 구성
- [ ] 알림 규칙 설정

## Phase 8: Documentation ✅

### 완료된 문서
- [x] README.md: 프로젝트 개요
- [x] GETTING_STARTED.md: 시작 가이드
- [x] ARCHITECTURE.md: 아키텍처 문서 (옵션 A 반영)
- [x] CODING_CONVENTION.md: 코딩 규칙
- [x] COMMIT_CONVENTION.md: 커밋 메시지 규칙
- [x] PROJECT_PROGRESS.md: 프로젝트 진행 상황

### 작성 예정 문서
- [ ] API_DOCUMENTATION.md: API 명세서
- [ ] DEPLOYMENT.md: 배포 가이드
- [ ] TROUBLESHOOTING.md: 문제 해결 가이드
- [ ] PERFORMANCE_TUNING.md: 성능 최적화 가이드

## 다음 단계 (Next Steps)

### 우선순위 높음
1. [ ] Inventory Service Entity 및 Repository 구현
2. [ ] Warehouse Service 기본 구조 생성
3. [ ] Delivery Service 기본 구조 생성
4. [ ] Notification Service 기본 구조 생성
5. [ ] Analytics Service 기본 구조 생성

### 우선순위 중간
1. [ ] Order Service 비즈니스 로직 확장
2. [ ] Kafka 이벤트 발행/구독 구현
3. [ ] API Gateway 인증/인가 설정
4. [ ] 재고 동시성 제어 구현

### 우선순위 낮음
1. [ ] 통합 테스트 작성
2. [ ] 성능 테스트 환경 구축
3. [ ] CI/CD 파이프라인 구축
4. [ ] Kubernetes 배포 설정

## 아키텍처 결정 사항 (ADR)

### ADR-001: 6개 서비스 구조 채택 (옵션 A)
- **결정일**: 2025-01-27
- **배경**: 초기 계획은 12개의 세분화된 서비스였으나, 학습 프로젝트의 현실적 관리 가능성 고려
- **결정**: 도메인별 통합 서비스 구조 채택
  - Order + Customer 통합
  - Warehouse 전체 통합 (입고/피킹/적치)
  - Delivery 전체 통합 (차량/기사/배송)
- **장점**:
  - 관리 복잡도 감소
  - 실무에서 더 일반적인 접근
  - 초기 개발 속도 향상
- **단점**:
  - 서비스 간 책임 분리가 덜 명확
  - 향후 확장 시 분리 필요 가능성
- **향후 계획**: 트래픽 증가 시 병목 서비스만 선별적 분리

### ADR-002: UUID 기반 PK 전략 채택
- **결정일**: 2025-01-27
- **배경**: MSA 환경에서 PK 전략 선택 (UUID vs BIGSERIAL)
- **결정**: 모든 서비스에서 UUID 사용
- **장점**:
  - 분산 환경에서 중복 없이 ID 생성 가능
  - 서비스 간 데이터 병합 시 충돌 방지
  - 외부 노출 시 정보 유추 불가 (보안)
  - MSA 학습 목적에 부합
- **단점**:
  - 인덱스 크기 증가 (16 bytes vs 8 bytes)
  - BIGSERIAL 대비 가독성 떨어짐
- **특이 사항**: 
  - Warehouse Service는 단일 창고 내부 관리 특성상 BIGSERIAL이 더 적합할 수 있으나, MSA 일관성을 위해 UUID 채택
  - BaseEntity에 주석으로 명시

### ADR-003: BaseEntity + AuditorAware 패턴 채택
- **결정일**: 2025-01-27
- **배경**: 모든 Entity에 Audit 정보 (생성자/수정자/생성일/수정일) 필요
- **결정**: Spring Data JPA의 @MappedSuperclass와 AuditorAware 사용
- **장점**:
  - 코드 중복 제거
  - 자동 Audit 정보 관리
  - 향후 User Service 추가 시 쉽게 확장 가능
- **현재 구현**:
  - AuditorAware는 "SYSTEM" 반환
  - 향후 Spring Security Context 또는 API Gateway 헤더에서 사용자 ID 추출 예정

### ADR-004: Common Service 통합 구조 채택
- **결정일**: 2025-01-27
- **배경**: User, Code, File 등 공통 기능을 별도 서비스로 분리할지, 하나의 서비스로 통합할지 결정 필요
- **결정**: 단일 Common Service로 통합하되 도메인별 패키지 분리
- **장점**:
  - 관리 복잡도 감소 (단일 서비스, 단일 DB)
  - 향후 도메인 추가 용이 (User, Code, File, Audit 등)
  - 인프라 리소스 절약 (DB, 포트 등)
  - 공통 기능 간 트랜잭션 처리 용이
- **단점**:
  - 서비스 크기가 커질 수 있음
  - 특정 도메인만 스케일링하기 어려움
- **구조**:
  ```
  common-service/
  ├── user/       # 사용자 관리
  ├── code/       # 공통 코드 (향후)
  ├── file/       # 파일 관리 (향후)
  └── shared/     # 공통 Entity
  ```
- **향후 계획**: 특정 도메인의 트래픽이 급증하면 분리 고려

### ADR-005: DTO 네이밍 컨벤션
- **결정일**: 2025-01-27
- **배경**: DTO 클래스 네이밍의 일관성 필요
- **결정**: "DTO"는 항상 대문자로 작성
  - ✅ 올바른 예: `UserRequestDTO`, `UserResponseDTO`, `OrderDTO`
  - ❌ 잘못된 예: `UserRequestDto`, `UserResponseDto`, `OrderDto`
- **근거**:
  - DTO는 약어(Data Transfer Object)이므로 모두 대문자로 표기하는 것이 표준
  - Java 네이밍 컨벤션에서 약어는 모두 대문자 권장 (예: URL, HTTP, JSON)
- **적용 범위**: 
  - 모든 서비스의 DTO 클래스명
  - 패키지명은 소문자 유지 (com.scm.common.user.web.dto)

### ADR-006: API 버전 관리 전략
- **결정일**: 2025-01-27
- **배경**: API의 하위 호환성 유지 및 점진적 업그레이드를 위한 버전 관리 필요
- **결정**: URL 경로에 버전 명시 (`/{service-name}/api/{version}/`)
  - ✅ 올바른 예: `/common-service/api/v1/`, `/order-service/api/v1/`, `/delivery-service/api/v1/`
  - ❌ 잘못된 예: `/api/v1/users`, `/api/users`, `/users`, `/v1/api/users`
- **버전 관리 정책**:
  - v1: 초기 버전 (현재)
  - 주요 변경 시 v2, v3 등으로 증가
  - 기존 버전은 최소 6개월 이상 유지 (Deprecated)
  - 버전별로 별도 Controller/Resource 클래스 생성
- **예시**:
  ```
  /api/common-service/v1/users          # v1 API
  /api/common-service/v2/users          # v2 API (향후)
  ```
- **장점**:
  - 명확한 API 버전 식별
  - 하위 호환성 유지하며 새 기능 추가 가능
  - 클라이언트가 원하는 버전 선택 가능
- **향후 계획**: v2 이상 버전 출시 시 변경 로그 및 마이그레이션 가이드 제공

## 이슈 및 개선사항

### 현재 이슈
- 없음

### 개선 필요 사항
1. [ ] settings.gradle 모듈 정의 업데이트 (6개 서비스로)
2. [ ] Docker Compose 포트 매핑 조정
3. [ ] API Gateway 라우팅 규칙 완성
4. [ ] 로깅 설정 표준화

## 회고 및 배운 점

### 2025-01-27 (1차: 프로젝트 초기 설정)
- **완료**: 
  - 프로젝트 초기 구조 및 Infrastructure 서비스 설정 완료
  - Order Service 완전 구현 (Entity, Repository, Service, REST API)
  - 아키텍처 문서 옵션 A로 업데이트
- **배운 점**: 
  - Spring Cloud 2023.0.0 버전의 의존성 관리 방법
  - Gradle 멀티 모듈 프로젝트 구조화
  - Docker Compose를 활용한 Database per Service 패턴 구현
  - 실무적 서비스 분리 기준 (너무 세분화하지 않기)
- **다음 목표**: WMS, TMS, Common Services 기본 구조 완성

### 2025-01-27 (2차: Entity 구현 완료)
- **완료**:
  - 전체 6개 서비스의 Entity/Document 구현 완료 (총 22개)
  - BaseEntity 및 AuditorAware 패턴 적용
  - UUID 기반 PK 전략 확정
  - 모든 서비스에 JPA Auditing 설정 완료
- **배운 점**:
  - MSA에서 일관된 Entity 설계의 중요성
  - BaseEntity 추상 클래스를 통한 공통 코드 관리
  - MongoDB Document 설계 (Analytics Service)
  - Warehouse Service는 BIGSERIAL이 더 적합하지만 MSA 일관성을 위해 UUID 채택
- **다음 목표**: Repository 인터페이스 구현 및 Service 레이어 구축

### 2025-01-27 (3차: Common Service 구현 완료)
- **완료**:
  - Common Service 신규 생성 (User 관리 + 향후 확장 가능 구조)
  - User Entity 및 Role Enum 구현
  - UserRepository, UserService, UserResource (REST API) 완전 구현
  - Spring Security 기본 설정 (PasswordEncoder, BCrypt)
  - Docker Compose에 common-db 추가 (Port: 5436)
  - API Gateway 라우팅 설정 추가 (/api/users/**)
  - settings.gradle에 common-service 모듈 추가
- **배운 점**:
  - 확장 가능한 Common Service 설계 (User, Code, File 등 도메인 추가 가능)
  - Spring Security PasswordEncoder를 활용한 비밀번호 암호화
  - MSA에서 횡단 관심사(Cross-Cutting Concerns) 관리 방법
  - Common Service가 다른 서비스의 인증/인가 기반이 됨
- **다음 목표**: JWT 토큰 발급 및 검증 기능 구현, API Gateway JWT 필터 추가

### 2025-01-27 (4차: Docker Compose 버전 명시화 및 Kafka UI 추가)
- **완료**:
  - Docker Compose 이미지 버전 명시 (:latest → 특정 버전)
    - Kafka UI: v0.7.2
    - Zipkin: 3.0
    - Prometheus: v2.48.1
    - Grafana: 10.2.3
  - Kafka UI 서비스 추가 (Port: 8989)
  - Kafka 클러스터 연결 설정 (scm-kafka-cluster)
  - Zookeeper 연동 설정
- **배운 점**:
  - 프로덕션 환경에서는 :latest 태그 사용을 지양하고 특정 버전 명시 필요
  - Kafka UI를 통해 토픽, 메시지, 컨슈머 그룹을 웹 UI로 모니터링 가능
  - 이벤트 기반 아키텍처 디버깅을 위한 필수 도구
  - provectuslabs/kafka-ui 이미지 활용
- **다음 목표**: Repository 및 Service 레이어 구현 시작

## Entity 설계 세부 사항

### Order Service (4개 Entity) ✅
1. **Order**: 주문 정보 (UUID, 주문번호, 고객ID, 상태, 총금액)
2. **OrderItem**: 주문 항목 (상품코드, 수량, 단가)
3. **Customer**: 고객 정보 (이름, 이메일, 전화번호, 고객유형)
4. **Address**: 주소 정보 (배송지/청구지, 우편번호, 주소)

### Inventory Service (2개 Entity) ✅
1. **Inventory**: 재고 정보 (창고ID, 상품코드, 가용수량, 할당수량, 안전재고)
2. **StockMovement**: 재고 이동 이력 (입고/출고/조정/예약/해제)

### Warehouse Service (8개 Entity) ✅
1. **Warehouse**: 창고 정보 (창고코드, 이름, 주소, 유형, 용량)
2. **Zone**: 창고 구역 (구역코드, 유형: 입고/보관/피킹/포장/출고)
3. **Location**: 로케이션 (위치코드, 유형: 선반/팔레트/바닥)
4. **InboundOrder**: 입고 주문 (입고번호, 공급업체, 예정일)
5. **InboundItem**: 입고 항목 (상품코드, 예정수량, 실제수량, QC 상태)
6. **PickingList**: 피킹 리스트 (주문ID, 작업자, 우선순위)
7. **PickingItem**: 피킹 항목 (상품코드, 위치, 수량)
8. **Worker**: 작업자 (작업자코드, 이름, 역할: 피커/패커/QC/관리자)

### Delivery Service (5개 Entity) ✅
1. **DeliveryOrder**: 배송 주문 (송장번호, 주문ID, 차량/기사, 배송지, 상태)
2. **DeliveryRoute**: 배송 경로 (경로명, 차량/기사, 일자, 총거리)
3. **Vehicle**: 차량 (차량번호, 유형, 적재량, 상태)
4. **VehicleLocation**: 차량 위치 추적 (위도, 경도, 속도, 기록시각)
5. **Driver**: 배송기사 (기사코드, 이름, 면허번호, 상태)

### Notification Service (2개 Entity) ✅
1. **Notification**: 알림 (수신자, 유형, 채널, 제목, 메시지, 발송/읽음 시각)
2. **NotificationTemplate**: 알림 템플릿 (템플릿코드, 채널, 제목/본문 템플릿)

### Analytics Service (3개 Document) ✅
1. **OrderAnalytics**: 주문 분석 데이터 (주문ID, 일자, 고객, 금액, 처리시간)
2. **DeliveryPerformance**: 배송 성과 데이터 (배송ID, 기사, 차량, 거리, 시간, 정시여부)
3. **WarehouseOperations**: 창고 작업 데이터 (작업유형, 창고, 작업자, 처리건수, 소요시간)

### Common Service (2개 Entity) ✅
1. **User**: 사용자 정보 (username, email, password, role, enabled, lastLoginAt)
2. **Role (Enum)**: 사용자 역할 (ADMIN, MANAGER, OPERATOR, CUSTOMER)

## 참고 링크

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Microservices Patterns](https://microservices.io/patterns)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Database per Service Pattern](https://microservices.io/patterns/data/database-per-service.html)

---

**마지막 업데이트**: 2025-01-27 (Docker 이미지 버전 명시화 및 Kafka UI 추가)
**업데이트 담당**: SCM Team
**총 Entity/Document 수**: 24개 (Order: 4, Inventory: 2, Warehouse: 8, Delivery: 5, Notification: 2, Analytics: 3, Common: 2)
