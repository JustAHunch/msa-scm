# Coding Convention

MSA-SCM 프로젝트의 코딩 규칙 및 컨벤션입니다.

## Java 코딩 스타일

### 1. 네이밍 규칙

#### 클래스명
- **PascalCase** 사용
- 명사형 사용
- 의미가 명확해야 함

```java
// Good
public class OrderService { }
public class CustomerRepository { }

// Bad
public class orderservice { }
public class Repo { }
```

#### 메서드명
- **camelCase** 사용
- 동사로 시작
- 의미를 명확히 표현

```java
// Good
public Order createOrder() { }
public void cancelOrder(Long orderId) { }
public boolean isOrderValid() { }

// Bad
public Order order() { }
public void cancel() { }
public boolean valid() { }
```

#### 변수명
- **camelCase** 사용
- 명확한 의미 전달
- 약어 지양

```java
// Good
private String customerName;
private List<OrderItem> orderItems;

// Bad
private String cn;
private List<OrderItem> items;
```

#### 상수명
- **UPPER_SNAKE_CASE** 사용
- static final로 선언

```java
// Good
public static final int MAX_RETRY_COUNT = 3;
public static final String DEFAULT_CURRENCY = "KRW";

// Bad
public static final int maxRetryCount = 3;
```

### 2. 패키지 구조

각 서비스는 다음 패키지 구조를 따릅니다:

```
com.logistics.scm.{domain}.{service}/
├── common/                     # 공통 컴포넌트
│   ├── BaseEntity.java        # 공통 Entity 추상 클래스
│   └── exception/             # 공통 예외 인프라
│       ├── BaseException.java
│       ├── BusinessException.java
│       ├── EntityNotFoundException.java
│       ├── InvalidInputException.java
│       ├── DuplicateEntityException.java
│       ├── UnauthorizedException.java
│       ├── ErrorCode.java
│       └── GlobalExceptionHandler.java
├── config/                    # 설정 클래스
│   ├── JpaConfig.java
│   ├── SwaggerConfig.java
│   └── ...
├── domain/                    # 도메인별 패키지
│   └── {domain}/             # 예: order, customer, inventory
│       ├── dto/
│       │   ├── request/      # 요청 DTO
│       │   │   └── *Request.java
│       │   ├── response/     # 응답 DTO
│       │   │   ├── ApiResponse.java      # 공통 성공 응답 래퍼
│       │   │   ├── ErrorResponse.java    # 공통 에러 응답
│       │   │   └── *Response.java
│       │   └── *DTO.java     # 기타 DTO
│       ├── entity/           # JPA 엔티티
│       │   └── *.java
│       ├── exception/        # 도메인 특화 예외
│       │   └── *Exception.java
│       ├── repository/       # 데이터 접근 계층
│       │   └── *Repository.java
│       ├── resource/         # REST API 컨트롤러
│       │   └── *Resource.java
│       └── service/          # 비즈니스 로직
│           ├── *Service.java
│           └── *ServiceImpl.java
└── event/                    # 이벤트 클래스
    ├── *Event.java
    └── listener/
        └── *EventListener.java
```

**주요 변경 사항**:
- `controller/` → `domain.{domain}.resource/`: 도메인별 리소스 관리
- `dto/` → `domain.{domain}.dto/`: 도메인별 DTO 관리
- `dto/request/`, `dto/response/`: 요청/응답 DTO 명확히 분리
- `common.exception/`: 공통 예외 인프라 중앙 관리
- `domain.{domain}.exception/`: 도메인별 특화 예외 관리
- `response/ApiResponse.java`: 공통 성공 응답 래퍼 추가
- `response/ErrorResponse.java`: 공통 에러 응답 추가

**상세 가이드**: [공통 응답 규격 및 예외 처리 가이드](./API_RESPONSE_GUIDE.md)

### 3. 레이어별 명명 규칙

#### Resource (Controller)
- **`*Resource`** 접미사 사용 (권장)
- RESTful 리소스 중심으로 명명
- 패키지: `domain.{domain}.resource`

```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderResource {
    // API 엔드포인트
}
```

#### Service
- **`*Service`** 접미사 사용
- 인터페이스와 구현체 분리
- 패키지: `domain.{domain}.service`

```java
public interface OrderService {
    Order createOrder(Order order);
}

@Service
public class OrderServiceImpl implements OrderService {
    // 구현
}
```

#### Repository
- **`*Repository`** 접미사 사용
- Spring Data JPA 인터페이스 사용
- 패키지: `domain.{domain}.repository`

```java
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerId(UUID customerId);
}
```

#### DTO
- **Request DTO**: `*RequestDTO` 접미사
- **Response DTO**: `*ResponseDTO` 접미사
- 패키지: `domain.{domain}.dto.request` 또는 `domain.{domain}.dto.response`

```java
// Request DTO (domain.order.dto.request 패키지)
public class OrderCreateRequestDTO {
    private UUID customerId;
    private List<OrderItemRequestDTO> items;
}

// Response DTO (domain.order.dto.response 패키지)
public class OrderResponseDTO {
    private UUID id;
    private String orderNumber;
    private String status;
}
```

#### 공통 응답 래퍼
- **ApiResponse<T>**: 성공 응답 래퍼
- **ErrorResponse**: 에러 응답
- 패키지: `domain.{domain}.dto.response`

```java
// 성공 응답
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrder(@PathVariable UUID id) {
    Order order = orderService.loadOrderById(id);
    OrderResponseDTO response = OrderResponseDTO.from(order);
    return ResponseEntity.ok(ApiResponse.success(response));
}

// 에러 응답은 GlobalExceptionHandler가 자동 처리
```

**상세 가이드**: [공통 응답 규격 및 예외 처리 가이드](./API_RESPONSE_GUIDE.md)

### 4. 어노테이션 사용

#### Lombok 활용
```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ORDER_TB")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String orderNumber;
    private OrderStatus status;
}
```

#### Validation
```java
@Data
public class CreateOrderRequest {
    @NotNull(message = "고객 ID는 필수입니다")
    private Long customerId;
    
    @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
    private List<OrderItemRequest> items;
    
    @NotBlank(message = "배송지 주소는 필수입니다")
    private String deliveryAddress;
}
```

### 5. 주석 작성

#### JavaDoc
- 모든 public 클래스와 메서드에 JavaDoc 작성
- 파라미터와 반환값 설명

```java
/**
 * 주문을 생성합니다.
 * 
 * @param request 주문 생성 요청 정보
 * @return 생성된 주문 정보
 * @throws InsufficientStockException 재고 부족 시
 */
public Order createOrder(CreateOrderRequest request) {
    // 구현
}
```

#### 인라인 주석
- 복잡한 로직에만 작성
- 코드로 설명 가능하면 주석 생략

```java
// Good - 필요한 경우에만
// 재고 확인 후 예약 처리 (동시성 제어를 위한 락 사용)
synchronized (inventory) {
    checkStock(productId, quantity);
    reserveStock(productId, quantity);
}

// Bad - 불필요한 주석
// 고객 ID 가져오기
Long customerId = request.getCustomerId();
```

### 6. 예외 처리

프로젝트는 통일된 예외 처리 체계를 사용합니다. 상세한 내용은 [공통 응답 규격 및 예외 처리 가이드](./API_RESPONSE_GUIDE.md)를 참조하세요.

#### 공통 예외 클래스 계층
```
BaseException (최상위)
├── BusinessException (비즈니스 로직 예외)
├── EntityNotFoundException (리소스 없음)
├── InvalidInputException (잘못된 입력)
├── DuplicateEntityException (중복 데이터)
└── UnauthorizedException (인증/인가 실패)
```

#### 커스텀 예외 작성
도메인별 예외는 위 공통 예외를 상속받아 작성합니다.

```java
// domain.order.exception 패키지
@Getter
public class OrderNotFoundException extends EntityNotFoundException {
    private final UUID orderId;

    public OrderNotFoundException(UUID orderId) {
        super(ErrorCode.ORDER_NOT_FOUND);
        this.orderId = orderId;
    }

    @Override
    public String getMessage() {
        return String.format("%s (주문ID: %s)", super.getMessage(), orderId);
    }
}
```

#### Service에서 예외 발생
```java
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;

    @Override
    public Order loadOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
```

#### 전역 예외 처리
GlobalExceptionHandler가 모든 예외를 자동으로 ErrorResponse로 변환합니다.

```java
// common.exception 패키지
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponse> handleBaseException(
            BaseException e, HttpServletRequest request) {
        
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }
}
```

#### ErrorCode Enum
각 서비스는 고유한 에러 코드 prefix를 사용합니다.

```java
// common.exception 패키지
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러 (C001~C999)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다."),

    // 주문 서비스 에러 (O001~O999)
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O003", "유효하지 않은 주문 상태입니다."),
    ORDER_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "O005", "주문 취소에 실패했습니다."),

    // 재고 서비스 에러 (I001~I999)
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "재고를 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "I002", "재고가 부족합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
```

**상세 가이드**: [공통 응답 규격 및 예외 처리 가이드](./API_RESPONSE_GUIDE.md)

### 7. 로깅

#### SLF4J 사용
```java
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    
    public Order createOrder(CreateOrderRequest request) {
        log.info("주문 생성 시작: customerId={}", request.getCustomerId());
        
        try {
            Order order = processOrder(request);
            log.info("주문 생성 완료: orderId={}, orderNumber={}", 
                     order.getId(), order.getOrderNumber());
            return order;
        } catch (Exception e) {
            log.error("주문 생성 실패: customerId={}", request.getCustomerId(), e);
            throw e;
        }
    }
}
```

#### 로그 레벨
- **ERROR**: 시스템 장애, 예외 발생
- **WARN**: 주의가 필요한 상황
- **INFO**: 주요 비즈니스 로직 실행
- **DEBUG**: 개발/디버깅 정보
- **TRACE**: 상세한 실행 흐름

### 8. REST API 규칙

#### URL 설계
```java
// Good
GET    /api/v1/orders              # 주문 목록 조회
POST   /api/v1/orders              # 주문 생성
GET    /api/v1/orders/{id}         # 특정 주문 조회
PUT    /api/v1/orders/{id}         # 주문 수정
DELETE /api/v1/orders/{id}         # 주문 삭제
GET    /api/v1/orders/{id}/items   # 주문 항목 조회

// Bad
GET    /api/getOrders
POST   /api/createOrder
GET    /api/order-detail?id=1
```

#### 응답 형식 (공통 규격)
모든 성공 응답은 `ApiResponse<T>`로, 모든 에러 응답은 `ErrorResponse`로 통일합니다.

```java
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderResource {

    private final OrderService orderService;

    /**
     * 주문 조회 - 성공 응답 (200 OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrder(@PathVariable UUID id) {
        Order order = orderService.loadOrderById(id);
        OrderResponseDTO response = OrderResponseDTO.from(order);
        
        return ResponseEntity.ok(
            ApiResponse.success(response)
        );
    }

    /**
     * 주문 생성 - 성공 응답 (201 Created)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderCreateRequestDTO request) {
        Order order = Order.from(request);
        Order createdOrder = orderService.createOrder(order);
        OrderResponseDTO response = OrderResponseDTO.from(createdOrder);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "주문이 성공적으로 생성되었습니다."));
    }

    /**
     * 주문 취소 - 성공 응답 (200 OK)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> cancelOrder(
            @PathVariable UUID id,
            @Valid @RequestBody OrderCancelRequestDTO request) {
        Order cancelledOrder = orderService.cancelOrder(id, request.getCancelReason());
        OrderResponseDTO response = OrderResponseDTO.from(cancelledOrder);
        
        return ResponseEntity.ok(
            ApiResponse.success(response, "주문이 성공적으로 취소되었습니다.")
        );
    }
}
```

#### 응답 JSON 예시

**성공 응답 (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "orderNumber": "ORD-20260205-000000001",
    "status": "CREATED"
  },
  "timestamp": "2026-02-05T14:30:00"
}
```

**에러 응답 (404 Not Found)**
```json
{
  "code": "O001",
  "message": "주문을 찾을 수 없습니다.",
  "status": 404,
  "timestamp": "2026-02-05T14:30:00",
  "path": "/api/v1/orders/550e8400-e29b-41d4-a716-446655440000"
}
```

#### HTTP 상태 코드
- **200 OK**: 성공 (조회, 수정)
- **201 Created**: 생성 성공
- **204 No Content**: 성공 (응답 바디 없음)
- **400 Bad Request**: 잘못된 요청
- **404 Not Found**: 리소스 없음
- **409 Conflict**: 리소스 충돌
- **500 Internal Server Error**: 서버 오류

**상세 가이드**: [공통 응답 규격 및 예외 처리 가이드](./API_RESPONSE_GUIDE.md)

### 9. 코드 포맷팅

#### 들여쓰기
- 4 spaces (탭 사용 금지)

#### 중괄호
- K&R 스타일 사용

```java
// Good
public void method() {
    if (condition) {
        // code
    } else {
        // code
    }
}

// Bad
public void method()
{
    if (condition)
    {
        // code
    }
}
```

#### 한 줄 길이
- 최대 120자

#### 메서드 길이
- 최대 50줄 권장
- 너무 길면 분리 고려

### 10. 테스트 코드

#### 테스트 메서드 명명
```java
@Test
void createOrder_Success() {
    // given
    CreateOrderRequest request = CreateOrderRequest.builder()
        .customerId(1L)
        .items(List.of(new OrderItemRequest(1L, 2)))
        .build();
    
    // when
    Order order = orderService.createOrder(request);
    
    // then
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
}

@Test
void createOrder_WhenInsufficientStock_ThrowsException() {
    // given - when - then
}
```

#### Given-When-Then 패턴
```java
@Test
void testExample() {
    // Given: 테스트 준비
    User user = new User("test@test.com");
    
    // When: 테스트 실행
    boolean result = user.isValid();
    
    // Then: 결과 검증
    assertThat(result).isTrue();
}
```

## Git Convention

### 브랜치 전략
- `main`: 프로덕션 브랜치
- `develop`: 개발 브랜치
- `feature/*`: 기능 개발
- `hotfix/*`: 긴급 수정

### 커밋 메시지
상세 내용은 [COMMIT_CONVENTION.md](COMMIT_CONVENTION.md) 참고

## 참고 자료

- [공통 응답 규격 및 예외 처리 가이드](./EXCEPTION_HANDLING_GUIDE.md) ⭐ **필독**
- [커밋 컨벤션](./COMMIT_CONVENTION.md)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Clean Code](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)
