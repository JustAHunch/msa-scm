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
├── controller/      # REST API 컨트롤러
├── service/         # 비즈니스 로직
├── repository/      # 데이터 접근 계층
├── entity/          # JPA 엔티티
├── dto/             # 데이터 전송 객체
│   ├── request/     # 요청 DTO
│   └── response/    # 응답 DTO
├── mapper/          # Entity ↔ DTO 변환
├── exception/       # 커스텀 예외
├── config/          # 설정 클래스
└── event/           # 이벤트 클래스
```

### 3. 레이어별 명명 규칙

#### Controller
- **`*Resource`** 또는 **`*Controller`** 접미사 사용
- RESTful 리소스 중심으로 명명

```java
@RestController
@RequestMapping("/api/orders")
public class OrderResource {
    // API 엔드포인트
}
```

#### Service
- **`*Service`** 접미사 사용
- 인터페이스와 구현체 분리

```java
public interface OrderService {
    Order createOrder(OrderRequest request);
}

@Service
public class OrderServiceImpl implements OrderService {
    // 구현
}
```

#### Repository
- **`*Repository`** 접미사 사용
- Spring Data JPA 인터페이스 사용

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
}
```

#### DTO
- **Request DTO**: `*Request` 접미사
- **Response DTO**: `*Response` 접미사

```java
public class CreateOrderRequest {
    private Long customerId;
    private List<OrderItemRequest> items;
}

public class OrderResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
}
```

### 4. 어노테이션 사용

#### Lombok 활용
```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
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

#### 커스텀 예외
```java
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("주문을 찾을 수 없습니다. Order ID: " + orderId);
    }
}
```

#### 전역 예외 처리
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

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
GET    /api/orders              # 주문 목록 조회
POST   /api/orders              # 주문 생성
GET    /api/orders/{id}         # 특정 주문 조회
PUT    /api/orders/{id}         # 주문 수정
DELETE /api/orders/{id}         # 주문 삭제
GET    /api/orders/{id}/items   # 주문 항목 조회

// Bad
GET    /api/getOrders
POST   /api/createOrder
GET    /api/order-detail?id=1
```

#### HTTP 상태 코드
```java
@PostMapping
public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
    Order order = orderService.createOrder(request);
    OrderResponse response = orderMapper.toResponse(order);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

- **200 OK**: 성공 (조회, 수정)
- **201 Created**: 생성 성공
- **204 No Content**: 성공 (응답 바디 없음)
- **400 Bad Request**: 잘못된 요청
- **404 Not Found**: 리소스 없음
- **500 Internal Server Error**: 서버 오류

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

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Clean Code](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)
