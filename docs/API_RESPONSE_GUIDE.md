# MSA 서비스 API 응답 규격 가이드

> 모든 서비스에 일관된 API 응답 체계를 적용하기 위한 가이드

## 📋 목차
- [개요](#개요)
- [패키지 구조](#패키지-구조)
- [구현 단계](#구현-단계)
- [ApiResponse 사용 가이드](#apiresponse-사용-가이드)
- [ErrorResponse 사용 가이드](#errorresponse-사용-가이드)
- [ErrorCode 설계 규칙](#errorcode-설계-규칙)
- [예외 클래스 작성 가이드](#예외-클래스-작성-가이드)
- [서비스별 적용 예시](#서비스별-적용-예시)

---

## 개요

### 목적
- **일관성**: 모든 서비스에서 동일한 응답 형식 제공 (성공/실패)
- **추적성**: 에러 코드를 통한 빠른 문제 파악
- **유지보수성**: 중앙 집중식 예외 및 응답 관리
- **개발 효율**: 공통 응답 래퍼를 통한 반복 코드 제거

### 핵심 원칙
1. **패키지 분리**: request/response 분리, 공통 예외(common.exception) vs 도메인 예외(exception)
2. **계층 구조**: BaseException → 특화 예외 → 도메인 예외
3. **에러 코드 체계**: 서비스별 prefix + 일련번호
4. **응답 래퍼**: ApiResponse<T>로 성공 응답 통일, ErrorResponse로 실패 응답 통일

---

## 패키지 구조

### 표준 디렉토리 구조
```
{service-name}/src/main/java/com/logistics/scm/{domain}/{service}/
├── common/
│   └── exception/              # 공통 예외 인프라
│       ├── BaseException.java
│       ├── BusinessException.java
│       ├── EntityNotFoundException.java
│       ├── InvalidInputException.java
│       ├── DuplicateEntityException.java
│       ├── UnauthorizedException.java
│       ├── ErrorCode.java
│       └── GlobalExceptionHandler.java
├── domain/
│   └── {domain}/
│       ├── dto/
│       │   ├── request/        # 요청 DTO
│       │   │   ├── {Domain}CreateRequest.java
│       │   │   └── {Domain}UpdateRequest.java
│       │   └── response/       # 응답 DTO
│       │       ├── ApiResponse.java        # 성공 응답 래퍼
│       │       ├── ErrorResponse.java      # 에러 응답 래퍼
│       │       ├── {Domain}Response.java
│       │       └── {Domain}ItemResponse.java
│       ├── entity/             # JPA 엔티티
│       ├── exception/          # 도메인 특화 예외
│       │   ├── {Domain}NotFoundException.java
│       │   └── {Domain}BusinessException.java
│       ├── repository/         # 데이터 접근
│       ├── service/            # 비즈니스 로직
│       └── resource/           # REST API
```

### 패키지별 역할

| 패키지 | 역할 | 예시 |
|--------|------|------|
| `common.exception` | 공통 예외 인프라, 전역 핸들러 | BaseException, ErrorCode, GlobalExceptionHandler |
| `dto.request` | 요청 DTO | OrderCreateRequestDTO, OrderCancelRequestDTO |
| `dto.response` | 응답 DTO 및 래퍼 | ApiResponse, ErrorResponse, OrderResponseDTO |
| `exception` | 도메인 특화 예외 | OrderNotFoundException, InsufficientStockException |

---

## 구현 단계

### Step 1: 공통 예외 클래스 생성

#### 1.1 BaseException.java
```java
package com.logistics.scm.{domain}.{service}.common.exception;

import lombok.Getter;

/**
 * 시스템 통합 최상위 예외 클래스
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
```

#### 1.2 BusinessException.java
```java
package com.logistics.scm.{domain}.{service}.common.exception;

/**
 * 비즈니스 로직 예외
 * 일반적인 비즈니스 규칙 위반 시 발생
 */
public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
```

#### 1.3 EntityNotFoundException.java
```java
package com.logistics.scm.{domain}.{service}.common.exception;

/**
 * 엔티티 조회 실패 예외
 * 요청한 리소스를 찾을 수 없을 때 발생
 */
public class EntityNotFoundException extends BaseException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityNotFoundException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
```

#### 1.4 InvalidInputException.java
```java
package com.logistics.scm.{domain}.{service}.common.exception;

/**
 * 잘못된 입력값 예외
 * 유효하지 않은 요청 데이터가 전달되었을 때 발생
 */
public class InvalidInputException extends BaseException {

    public InvalidInputException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidInputException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
```

#### 1.5 DuplicateEntityException.java
```java
package com.logistics.scm.{domain}.{service}.common.exception;

/**
 * 중복 엔티티 예외
 * 이미 존재하는 데이터를 생성하려고 할 때 발생
 */
public class DuplicateEntityException extends BaseException {

    public DuplicateEntityException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateEntityException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
```

#### 1.6 UnauthorizedException.java
```java
package com.logistics.scm.{domain}.{service}.common.exception;

/**
 * 인증/인가 실패 예외
 * 인증되지 않았거나 권한이 없을 때 발생
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
```

---

### Step 2: ErrorCode Enum 생성

```java
package com.logistics.scm.{domain}.{service}.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 Enum
 * {서비스명} 전체 에러 코드 중앙 관리
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러 (C001~C999)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다."),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "C004", "이미 존재하는 데이터입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C005", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C006", "접근 권한이 없습니다."),

    // 서비스별 에러 ({PREFIX}001~{PREFIX}999)
    // 아래 섹션 참조

    private final HttpStatus status;
    private final String code;
    private final String message;
}
```

---

### Step 3: ApiResponse (성공 응답 래퍼) 생성

```java
package com.logistics.scm.{domain}.{service}.domain.{domain}.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * API 공통 응답 래퍼 클래스
 * 모든 성공 응답을 일관된 형식으로 제공
 *
 * @param <T> 응답 데이터 타입
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;
    private final LocalDateTime timestamp;

    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 성공 응답 생성 (메시지 포함)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 성공 응답 생성 (메시지만)
     */
    public static <T> ApiResponse<T> successWithMessage(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

---

### Step 4: ErrorResponse (에러 응답) 생성

```java
package com.logistics.scm.{domain}.{service}.domain.{domain}.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 에러 응답 DTO
 * 일관된 에러 응답 형식 제공
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String code;
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;
    private final String path;
    private final List<FieldError> errors;

    /**
     * 필드 에러 정보
     */
    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;
    }
}
```

---

### Step 5: GlobalExceptionHandler 생성

```java
package com.logistics.scm.{domain}.{service}.common.exception;

import com.logistics.scm.{domain}.{service}.domain.{domain}.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리기
 * 모든 예외를 중앙에서 처리하여 일관된 에러 응답 제공
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BaseException 처리
     * 커스텀 예외의 기본 처리
     */
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponse> handleBaseException(
            BaseException e, HttpServletRequest request) {

        log.error("BaseException: code={}, message={}",
                e.getErrorCode().getCode(), e.getMessage(), e);

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

    /**
     * Validation 예외 처리
     * @Valid 검증 실패 시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        log.error("Validation Error: {}", e.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .value(error.getRejectedValue() != null ?
                                error.getRejectedValue().toString() : "")
                        .reason(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT_VALUE.getCode())
                .message(ErrorCode.INVALID_INPUT_VALUE.getMessage())
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus().value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * IllegalArgumentException 처리
     * 잘못된 인자 전달 시 발생
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {

        log.error("IllegalArgumentException: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT_VALUE.getCode())
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 일반 예외 처리
     * 처리되지 않은 모든 예외의 최종 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {

        log.error("Unexpected Exception: ", e);

        ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
```

---

### Step 6: 도메인 예외 작성

도메인별 특화 예외는 `exception` 패키지에 작성합니다.

**예시: OrderNotFoundException**
```java
package com.logistics.scm.{domain}.{service}.domain.{domain}.exception;

import com.logistics.scm.{domain}.{service}.common.exception.EntityNotFoundException;
import com.logistics.scm.{domain}.{service}.common.exception.ErrorCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 주문 조회 실패 예외
 * 요청한 주문을 찾을 수 없을 때 발생
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
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

---

## ApiResponse 사용 가이드

### 기본 사용법

#### 1. 데이터와 함께 성공 응답
```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrder(@PathVariable UUID id) {
    Order order = orderService.loadOrderById(id);
    OrderResponseDTO response = OrderResponseDTO.from(order);
    
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

**응답 예시**:
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "orderNumber": "ORD-20260205-000000001",
    "status": "CREATED",
    "totalAmount": 150000.00
  },
  "timestamp": "2026-02-05T14:30:00"
}
```

#### 2. 메시지와 함께 성공 응답
```java
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
```

**응답 예시**:
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "orderNumber": "ORD-20260205-000000001"
  },
  "message": "주문이 성공적으로 생성되었습니다.",
  "timestamp": "2026-02-05T14:30:00"
}
```

#### 3. 데이터 없는 성공 응답
```java
@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable UUID id) {
    orderService.deleteOrder(id);
    
    return ResponseEntity.ok(
            ApiResponse.successWithMessage("주문이 성공적으로 삭제되었습니다.")
    );
}
```

**응답 예시**:
```json
{
  "success": true,
  "message": "주문이 성공적으로 삭제되었습니다.",
  "timestamp": "2026-02-05T14:30:00"
}
```

---

## ErrorResponse 사용 가이드

### 자동 에러 응답 (GlobalExceptionHandler)

GlobalExceptionHandler가 자동으로 처리하므로 Service 레이어에서는 예외만 발생시키면 됩니다.

#### 1. 리소스 없음 (404)
```java
@Service
public class OrderServiceImpl implements OrderService {
    
    @Override
    public Order loadOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
```

**에러 응답**:
```json
{
  "code": "O001",
  "message": "주문을 찾을 수 없습니다.",
  "status": 404,
  "timestamp": "2026-02-05T14:30:00",
  "path": "/api/v1/orders/550e8400-e29b-41d4-a716-446655440000"
}
```

#### 2. Validation 에러 (400)
```java
@PostMapping
public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
        @Valid @RequestBody OrderCreateRequestDTO request) {
    // Validation 실패 시 GlobalExceptionHandler가 자동 처리
}
```

**에러 응답**:
```json
{
  "code": "C002",
  "message": "잘못된 입력값입니다.",
  "status": 400,
  "timestamp": "2026-02-05T14:30:00",
  "path": "/api/v1/orders",
  "errors": [
    {
      "field": "customerId",
      "value": "",
      "reason": "고객 ID는 필수입니다"
    },
    {
      "field": "items",
      "value": "[]",
      "reason": "주문 항목은 최소 1개 이상이어야 합니다"
    }
  ]
}
```

#### 3. 비즈니스 로직 에러 (400)
```java
@Service
public class OrderServiceImpl implements OrderService {
    
    @Override
    public Order cancelOrder(UUID orderId, String cancelReason) {
        Order order = loadOrderById(orderId);
        
        if (order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(orderId, order.getOrderStatus(), "주문 취소");
        }
        
        order.cancel();
        return orderRepository.save(order);
    }
}
```

**에러 응답**:
```json
{
  "code": "O003",
  "message": "유효하지 않은 주문 상태입니다.",
  "status": 400,
  "timestamp": "2026-02-05T14:30:00",
  "path": "/api/v1/orders/550e8400-e29b-41d4-a716-446655440000"
}
```

---

## ErrorCode 설계 규칙

### 에러 코드 체계

| Prefix | 서비스 | 범위 | 설명 |
|--------|--------|------|------|
| **C** | Common | C001~C999 | 공통 에러 |
| **O** | Order | O001~O999 | 주문 서비스 |
| **I** | Inventory | I001~I999 | 재고 서비스 |
| **W** | Warehouse | W001~W999 | 창고 서비스 |
| **D** | Delivery | D001~D999 | 배송 서비스 |
| **U** | User | U001~U999 | 사용자 서비스 |
| **N** | Notification | N001~N999 | 알림 서비스 |
| **A** | Analytics | A001~A999 | 분석 서비스 |

### 공통 에러 (모든 서비스 동일)
```java
// 공통 에러 (C001~C999)
INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다."),
DUPLICATE_ENTITY(HttpStatus.CONFLICT, "C004", "이미 존재하는 데이터입니다."),
UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C005", "인증이 필요합니다."),
FORBIDDEN(HttpStatus.FORBIDDEN, "C006", "접근 권한이 없습니다."),
```

### 에러 코드 작성 가이드

#### 1. NOT_FOUND 에러 (404)
```java
{ENTITY}_NOT_FOUND(HttpStatus.NOT_FOUND, "{PREFIX}001", "{엔티티}를 찾을 수 없습니다.")

// 예시
ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "재고를 찾을 수 없습니다."),
WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "창고를 찾을 수 없습니다."),
```

#### 2. BAD_REQUEST 에러 (400)
```java
INVALID_{DOMAIN}_{FIELD}(HttpStatus.BAD_REQUEST, "{PREFIX}00X", "유효하지 않은 {필드}입니다.")

// 예시
INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O003", "유효하지 않은 주문 상태입니다."),
INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "I005", "유효하지 않은 재고 수량입니다."),
```

#### 3. CONFLICT 에러 (409)
```java
{ENTITY}_ALREADY_EXISTS(HttpStatus.CONFLICT, "{PREFIX}01X", "이미 존재하는 {엔티티}입니다.")

// 예시
ORDER_ALREADY_EXISTS(HttpStatus.CONFLICT, "O010", "이미 존재하는 주문입니다."),
```

#### 4. 비즈니스 로직 에러
```java
{ACTION}_FAILED(HttpStatus.BAD_REQUEST, "{PREFIX}02X", "{작업}에 실패했습니다.")

// 예시
ORDER_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "O005", "주문 취소에 실패했습니다."),
STOCK_RESERVATION_FAILED(HttpStatus.BAD_REQUEST, "I006", "재고 예약에 실패했습니다."),
```

---

## 예외 클래스 작성 가이드

### 예외 선택 플로우차트

```
요청 처리 중 문제 발생
    │
    ├─ 리소스를 찾을 수 없는가?
    │   └─ YES → EntityNotFoundException 상속
    │
    ├─ 중복 데이터인가?
    │   └─ YES → DuplicateEntityException 상속
    │
    ├─ 입력값이 유효하지 않은가?
    │   └─ YES → InvalidInputException 상속
    │
    ├─ 인증/인가 문제인가?
    │   └─ YES → UnauthorizedException 상속
    │
    └─ 비즈니스 규칙 위반인가?
        └─ YES → BusinessException 상속
```

### 예외 클래스 템플릿

```java
package com.logistics.scm.{domain}.{service}.domain.{domain}.exception;

import com.logistics.scm.{domain}.{service}.common.exception.{부모Exception};
import com.logistics.scm.{domain}.{service}.common.exception.ErrorCode;
import lombok.Getter;

/**
 * {예외 설명}
 * {발생 조건}
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
public class {예외명}Exception extends {부모Exception} {

    // 추가 정보 필드 (선택사항)
    private final String additionalInfo;

    public {예외명}Exception(/* 필요한 파라미터 */) {
        super(ErrorCode.{에러코드});
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String getMessage() {
        // 상세 메시지 커스터마이징 (선택사항)
        return String.format("%s (추가정보: %s)", super.getMessage(), additionalInfo);
    }
}
```

---

## 서비스별 적용 예시

### Order Service (주문 서비스)

#### ErrorCode 정의
```java
// 주문 관련 (O001~O999)
ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "O002", "고객을 찾을 수 없습니다."),
INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O003", "유효하지 않은 주문 상태입니다."),
ORDER_ALREADY_CANCELLED(HttpStatus.CONFLICT, "O004", "이미 취소된 주문입니다."),
ORDER_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "O005", "주문 취소에 실패했습니다."),
ORDER_ALREADY_COMPLETED(HttpStatus.CONFLICT, "O007", "이미 완료된 주문입니다."),
```

#### Resource (Controller) 구현
```java
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderResource {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> load(@PathVariable UUID id) {
        Order order = orderService.loadOrderById(id);
        OrderResponseDTO response = OrderResponseDTO.from(order);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> create(
            @Valid @RequestBody OrderCreateRequestDTO request) {
        Order order = Order.from(request);
        Order createdOrder = orderService.createOrder(order);
        OrderResponseDTO response = OrderResponseDTO.from(createdOrder);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "주문이 성공적으로 생성되었습니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> cancel(
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

#### Service 구현
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

    @Override
    public Order cancelOrder(UUID orderId, String cancelReason) {
        Order order = loadOrderById(orderId);
        
        // 비즈니스 로직 검증
        if (order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(orderId, order.getOrderStatus(), "주문 취소");
        }
        
        order.cancel();
        return orderRepository.save(order);
    }
}
```

### Inventory Service (재고 서비스)

#### ErrorCode 정의
```java
// 재고 관련 (I001~I999)
INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "재고를 찾을 수 없습니다."),
INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "I002", "재고가 부족합니다."),
INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "I005", "유효하지 않은 재고 수량입니다."),
STOCK_RESERVATION_FAILED(HttpStatus.BAD_REQUEST, "I006", "재고 예약에 실패했습니다."),
```

#### 도메인 예외
```java
@Getter
public class InsufficientStockException extends BusinessException {
    private final String productCode;
    private final Integer requestedQuantity;
    private final Integer availableQuantity;

    public InsufficientStockException(String productCode, Integer requestedQuantity, Integer availableQuantity) {
        super(ErrorCode.INSUFFICIENT_STOCK);
        this.productCode = productCode;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String getMessage() {
        return String.format("%s (상품: %s, 요청: %d, 가용: %d)",
                super.getMessage(), productCode, requestedQuantity, availableQuantity);
    }
}
```

---

## 체크리스트

새 서비스에 API 응답 규격을 적용할 때 다음 체크리스트를 확인하세요.

### 필수 구현 항목

- [ ] `common.exception` 패키지 생성
- [ ] `BaseException` 작성
- [ ] `BusinessException`, `EntityNotFoundException`, `InvalidInputException` 등 작성
- [ ] `ErrorCode` enum 작성 (공통 + 서비스별)
- [ ] `GlobalExceptionHandler` 작성
- [ ] `dto/request` 패키지 생성
- [ ] `dto/response` 패키지 생성
- [ ] `ApiResponse<T>` 작성
- [ ] `ErrorResponse` 작성
- [ ] 도메인 예외 클래스 작성 (`exception` 패키지)

### 검증 항목

- [ ] 모든 Resource(Controller)가 ApiResponse<T> 사용하는가?
- [ ] 모든 예외가 적절한 상위 클래스를 상속하는가?
- [ ] ErrorCode에 서비스별 prefix가 올바르게 적용되었는가?
- [ ] GlobalExceptionHandler가 모든 예외를 처리하는가?
- [ ] 성공/실패 응답 형식이 일관되는가?
- [ ] 로그가 적절하게 남는가?
- [ ] HTTP 상태 코드가 올바른가?
- [ ] Swagger 문서화가 완료되었는가?

---

## 참고 자료

### Order Service 구현체
- 위치: `C:\study\git\msa-scm\oms\order-service\src\main\java\com\logistics\scm\oms\order`
- 실제 구현 코드를 참고하여 다른 서비스에 적용

### 관련 문서
- [Spring Boot Exception Handling Best Practices](https://www.baeldung.com/exception-handling-for-rest-with-spring)
- [RFC 7807: Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc7807)
- [CODING_CONVENTION.md](./CODING_CONVENTION.md) - 코딩 규칙

---

**작성일**: 2026-02-05  
**버전**: 1.0  
**작성자**: SCM Team
