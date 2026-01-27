# Commit Convention

MSA-SCM 프로젝트의 Git 커밋 메시지 작성 규칙입니다.

## 기본 구조

```
<type>(<scope>): <subject>

<body>

<footer>
```

## Type

커밋의 목적을 나타냅니다.

| Type | 설명 | 예시 |
|------|------|------|
| `feat` | 새로운 기능 추가 | feat(order): 주문 생성 API 구현 |
| `fix` | 버그 수정 | fix(inventory): 재고 차감 버그 수정 |
| `docs` | 문서 수정 | docs(readme): Getting Started 섹션 추가 |
| `style` | 코드 포맷팅, 세미콜론 누락 등 | style(order): 코드 포맷팅 적용 |
| `refactor` | 코드 리팩토링 | refactor(service): 주문 서비스 로직 개선 |
| `test` | 테스트 코드 추가/수정 | test(order): 주문 생성 테스트 추가 |
| `chore` | 빌드, 설정 파일 수정 | chore(deps): Spring Boot 버전 업데이트 |
| `perf` | 성능 개선 | perf(inventory): 재고 조회 쿼리 최적화 |
| `ci` | CI/CD 관련 변경 | ci(github): GitHub Actions 워크플로우 추가 |
| `build` | 빌드 시스템 수정 | build(gradle): Gradle 설정 변경 |
| `revert` | 이전 커밋 되돌리기 | revert: feat(order) 커밋 되돌림 |

## Scope

변경된 부분을 나타냅니다. (선택사항)

### 서비스별 Scope

```
infrastructure:
  - eureka
  - gateway
  - config

oms:
  - order
  - customer
  - inventory

wms:
  - warehouse
  - picking
  - inbound

tms:
  - delivery
  - vehicle
  - driver

common:
  - notification
  - analytics
  - integration
```

### 기능별 Scope

```
- api
- service
- repository
- dto
- entity
- controller
- config
- test
- docs
```

## Subject

커밋의 제목입니다.

### 규칙
1. **50자 이내**로 작성
2. **마침표 사용 안 함**
3. **명령형** 사용 (과거형 X)
4. **한글** 또는 **영문** 사용 가능
5. 첫 글자는 **소문자**

### 예시

```bash
# Good
feat(order): 주문 생성 API 구현
fix(inventory): 재고 차감 로직 수정
docs(api): API 명세서 업데이트

# Bad
feat(order): 주문 생성 API를 구현했습니다.  # 과거형 X
Fix(inventory): 재고 수정  # 첫 글자 대문자 X
feat(order): 주문 생성 API 구현.  # 마침표 X
```

## Body

커밋의 상세 내용입니다. (선택사항)

### 규칙
1. Subject와 **한 줄 띄우고** 작성
2. **72자**마다 줄바꿈
3. **무엇을, 왜** 변경했는지 설명
4. **어떻게**는 코드로 설명되므로 생략 가능

### 예시

```bash
feat(order): 주문 생성 API 구현

- 고객 정보 검증 로직 추가
- 재고 확인 및 예약 기능 구현
- 주문 번호 자동 생성 (UUID 기반)
- 주문 생성 이벤트 발행

주문 생성 시 재고가 부족한 경우 예외 발생하도록 처리
향후 분산 트랜잭션 적용 예정
```

## Footer

이슈 트래커 ID나 Breaking Change를 명시합니다. (선택사항)

### 이슈 참조

```bash
feat(order): 주문 취소 기능 구현

Resolves: #123
See also: #456, #789
```

### Breaking Change

```bash
feat(api): 주문 API 응답 구조 변경

BREAKING CHANGE: 
주문 응답 DTO 구조가 변경되었습니다.
기존 `orderItems` 필드가 `items`로 변경되었습니다.
클라이언트 코드 업데이트가 필요합니다.
```

## 전체 예시

### 예시 1: 새 기능 추가

```bash
feat(order): 주문 생성 API 구현

- POST /api/orders 엔드포인트 추가
- 주문 생성 요청 DTO 및 응답 DTO 정의
- 재고 확인 및 예약 로직 통합
- 주문 번호 UUID 기반 자동 생성

주문 생성 시 재고 부족 시 InsufficientStockException 발생
주문 확정 후 OrderCreatedEvent 발행하여 다른 서비스로 전파

Resolves: #45
```

### 예시 2: 버그 수정

```bash
fix(inventory): 동시성 문제로 인한 재고 차감 버그 수정

재고 차감 시 동시에 여러 요청이 들어올 경우
실제 재고보다 많이 차감되는 문제 발생

비관적 락(Pessimistic Lock)을 적용하여 해결
향후 Redis 분산 락 도입 검토 예정

Fixes: #78
```

### 예시 3: 리팩토링

```bash
refactor(service): 주문 서비스 레이어 구조 개선

- OrderService 인터페이스와 구현체 분리
- 주문 생성 로직을 별도 메서드로 추출
- 중복 코드 제거 및 가독성 향상

기능 변경 없이 코드 구조만 개선
```

### 예시 4: 문서 업데이트

```bash
docs(architecture): 마이크로서비스 아키텍처 문서 추가

- 전체 시스템 아키텍처 다이어그램
- 각 서비스별 책임과 역할 명시
- 서비스 간 통신 패턴 설명
- 데이터 흐름 및 이벤트 처리 설명
```

### 예시 5: 테스트 추가

```bash
test(order): 주문 생성 서비스 단위 테스트 추가

- 정상 주문 생성 테스트
- 재고 부족 시 예외 발생 테스트
- 잘못된 고객 ID 입력 시 예외 발생 테스트
- 빈 주문 항목 입력 시 예외 발생 테스트

테스트 커버리지 85%로 향상
```

### 예시 6: 의존성 업데이트

```bash
chore(deps): Spring Boot 3.2.0에서 3.2.1로 업데이트

보안 패치 및 버그 수정 포함
변경 로그: https://github.com/spring-projects/spring-boot/releases/tag/v3.2.1

See also: #92
```

### 예시 7: 설정 변경

```bash
chore(config): Eureka 헬스 체크 간격 설정 변경

서비스 인스턴스 등록 지연 문제 개선을 위해
헬스 체크 간격을 30초에서 10초로 단축

변경 파일: eureka-server/application.yml
```

## Commit 시 체크리스트

커밋하기 전에 다음 사항을 확인하세요:

- [ ] Type이 명확한가?
- [ ] Subject가 50자 이내인가?
- [ ] Subject에 마침표가 없는가?
- [ ] 명령형으로 작성했는가?
- [ ] Body에 무엇을, 왜 변경했는지 설명했는가?
- [ ] 관련 이슈 번호를 참조했는가?
- [ ] Breaking Change가 있다면 명시했는가?

## 잘못된 예시

### Bad Example 1: 너무 간략함
```bash
fix: bug fix
```

### Bad Example 2: 과거형 사용
```bash
feat(order): 주문 기능을 추가했습니다
```

### Bad Example 3: 여러 변경사항을 한 커밋에
```bash
feat(order): 주문 생성, 조회, 수정, 삭제 API 모두 구현
```
→ 각각 별도 커밋으로 분리해야 함

### Bad Example 4: Type 누락
```bash
주문 생성 API 구현
```

### Bad Example 5: 의미 없는 메시지
```bash
feat(order): update
```

## 좋은 예시

### Good Example 1: 명확한 목적
```bash
feat(order): 주문 목록 조회 API 구현

- 페이징 및 정렬 기능 추가
- 주문 상태별 필터링 지원
- 날짜 범위 검색 기능 구현
```

### Good Example 2: 상세한 설명
```bash
fix(inventory): 재고 동기화 이슈 해결

WMS에서 재고 업데이트 시 OMS 재고가 동기화되지 않는 문제
Kafka 이벤트 리스너 재연결 로직 추가로 해결

Fixes: #123
```

### Good Example 3: Breaking Change 명시
```bash
feat(api): 주문 API 스펙 v2로 변경

BREAKING CHANGE:
/api/orders 엔드포인트의 응답 구조 변경
- orderNumber: String → UUID 타입으로 변경
- createdDate → createdAt으로 필드명 변경
클라이언트 코드 업데이트 필요

Resolves: #234
```

## 참고 자료

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Angular Commit Message Guidelines](https://github.com/angular/angular/blob/main/CONTRIBUTING.md#commit)
- [Git Commit Best Practices](https://chris.beams.io/posts/git-commit/)
