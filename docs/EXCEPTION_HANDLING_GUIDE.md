# MSA 서비스 공통 응답 규격 및 예외 처리 가이드

> 모든 서비스에 일관된 응답 규격과 예외 처리 체계를 적용하기 위한 가이드

## 📋 목차
- [개요](#개요)
- [패키지 구조](#패키지-구조)
- [공통 응답 규격 (ApiResponse)](#공통-응답-규격-apiresponse)
- [에러 응답 규격 (ErrorResponse)](#에러-응답-규격-errorresponse)
- [예외 처리 구현 단계](#예외-처리-구현-단계)
- [ErrorCode 설계 규칙](#errorcode-설계-규칙)
- [예외 클래스 작성 가이드](#예외-클래스-작성-가이드)
- [REST API 응답 패턴](#rest-api-응답-패턴)
- [서비스별 적용 예시](#서비스별-적용-예시)

---

## 개요

### 목적
- **일관성**: 모든 서비스에서 동일한 성공/에러 응답 형식 제공
- **추적성**: 에러 코드를 통한 빠른 문제 파악
- **유지보수성**: 중앙 집중식 응답 및 예외 관리
- **개발 효율성**: 표준화된 응답 래퍼로 반복 코드 제거

### 핵심 원칙
1. **패키지 분리**: 공통 예외(common.exception) vs 도메인 예외(domain.exception)
2. **계층 구조**: BaseException → 특화 예외 → 도메인 예외
3. **에러 코드 체계**: 서비스별 prefix + 일련번호
4. **응답 래퍼**: 성공은 ApiResponse<T>, 실패는 ErrorResponse

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
│       │   │   └── *Request.java
│       │   ├── response/       # 응답 DTO
│       │   │   ├── ApiResponse.java      # 공통 성공 응답 래퍼
│       │   │   ├── ErrorResponse.java    # 공통 에러 응답
│       │   │   └── *Response.java
│       │   └── *DTO.java       # 기타 DTO
│       ├── entity/             # JPA 엔티티
│       ├── exception/          # 도메인 특화 예외
│       ├── repository/         # 데이터 접근 계층
│       ├── resource/           # REST API 컨트롤러
│       └── service/            # 비즈니스 로직
```

### 패키지별 역할

| 패키지 | 역할 | 예시 |
|--------|------|------|
| `common.exception` | 공통 예외 인프라, 전역 핸들러 | BaseException, ErrorCode |
| `domain.{domain}.dto.request` | 요청 DTO | OrderCreateRequestDTO |
| `domain.{domain}.dto.response` | 응답 DTO, 공통 래퍼 | ApiResponse, ErrorResponse |
| `domain.{domain}.exception` | 도메인 특화 예외 | OrderNotFoundException |

---

## 공통 응답 규격 (ApiResponse)

### ApiResponse<T> 구조

모든 성공 응답은 `ApiResponse<T>` 제네릭 래퍼로 감싸서 반환합니다.

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

### 응답 JSON 구조

#### 데이터 포함 응답
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "orderNumber": "ORD-20260205-0001",
    "status": "CREATED"
  },
  "timestamp": "2026-02-05T14:30:00"
}
```

#### 메시지 포함 응답
```json
{
  "success": true,
  "data": { ... },
  "message": "주문이 성공적으로 생성되었습니다.",
  "timestamp": "2026-02-05T14:30:00"
}
```

#### 데이터 없는 응답
```json
{
  "success": true,
  "timestamp": "2026-02-05T14:30:00"
}
```

---

## 에러 응답 규격 (ErrorResponse)

### ErrorResponse 구조

모든 에러 응답은 `ErrorResponse`로 통일합니다.

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

### 에러 JSON 구조

#### 일반 에러
```json
{
  "code": "O001",
  "message": "주문을 찾을 수 없습니다.",
  "status": 404,
  "timestamp": "2026-02-05T14:30:00",
  "path": "/api/v1/orders/550e8400-e29b-41d4-a716-446655440000"
}
```

#### Validation 에러 (필드 에러 포함)
```json
{
  "code": "C002",
  "message": "잘못된 입력값입니다.",
  "status": 400,
  "timestamp": "2026-02-05T14:30:00",
  "path": "/api/v1/orders",
  "errors": [
    {
      "field": "quantity",
      "value": "-5",
      "reason": "수량은 0보다 커야 합니다."
    }
  ]
}
```

---

## 예외 처리 구현 단계

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

### Step 3: GlobalExceptionHandler 생성

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

### Step 4: 도메인 예외 작성

도메인별 특화 예외는 `domain.{domain}.exception` 패키지에 작성합니다.

**예시: OrderNotFoundException**
```java
package com.logistics.scm.{domain}.{service}.domain.order.exception;

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

### 서비스별 에러 코드 예시

#### Order Service (O001~O999)
```java
// 주문 관련 (O001~O999)
ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "O002", "고객을 찾을 수 없습니다."),
INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O003", "유효하지 않은 주문 상태입니다."),
ORDER_ALREADY_CANCELLED(HttpStatus.CONFLICT, "O004", "이미 취소된 주문입니다."),
ORDER_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "O005", "주문 취소에 실패했습니다."),
INSUFFICIENT_PAYMENT(HttpStatus.BAD_REQUEST, "O006", "결제 금액이 부족합니다."),
ORDER_ALREADY_COMPLETED(HttpStatus.CONFLICT, "O007", "이미 완료된 주문입니다."),
```

#### Inventory Service (I001~I999)
```java
// 재고 관련 (I001~I999)
INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "재고를 찾을 수 없습니다."),
INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "I002", "재고가 부족합니다."),
STOCK_ALREADY_RESERVED(HttpStatus.CONFLICT, "I003", "이미 예약된 재고입니다."),
STOCK_MOVEMENT_FAILED(HttpStatus.BAD_REQUEST, "I004", "재고 이동에 실패했습니다."),
INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "I005", "유효하지 않은 재고 수량입니다."),
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

## REST API 응답 패턴

### Controller/Resource에서의 사용법

```java
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderResource {

    private final OrderService orderService;

    /**
     * 주문 조회 - 성공 응답 (데이터만)
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
     * 주문 생성 - 성공 응답 (데이터 + 메시지)
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
     * 주문 취소 - 성공 응답 (데이터 + 메시지)
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

    /**
     * 주문 삭제 - 성공 응답 (데이터 없음, 메시지만)
     */
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        
        return ResponseEntity.ok(
            ApiResponse.successWithMessage("주문이 영구적으로 삭제되었습니다.")
        );
    }
}
```

### Service Layer에서 예외 발생

```java
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public Order loadOrderById(UUID orderId) {
        // 예외는 자동으로 GlobalExceptionHandler가 처리
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    @Transactional
    public Order cancelOrder(UUID orderId, String cancelReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 비즈니스 규칙 검증
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(
                orderId, order.getStatus(), "주문 취소"
            );
        }

        order.cancel();
        return orderRepository.save(order);
    }
}
```

---

## 서비스별 적용 예시

### Order Service 완전 구현 예시

#### 1. 패키지 구조
```
oms/order-service/src/main/java/com/logistics/scm/oms/order/
├── common/
│   └── exception/
│       ├── BaseException.java
│       ├── BusinessException.java
│       ├── EntityNotFoundException.java
│       ├── InvalidInputException.java
│       ├── ErrorCode.java
│       └── GlobalExceptionHandler.java
└── domain/
    └── order/
        ├── dto/
        │   ├── request/
        │   │   ├── OrderCreateRequest.java
        │   │   └── OrderCancelRequest.java
        │   └── response/
        │       ├── ApiResponse.java
        │       ├── ErrorResponse.java
        │       ├── OrderResponse.java
        │       └── OrderItemResponse.java
        ├── entity/
        │   ├── Order.java
        │   └── OrderItem.java
        ├── exception/
        │   ├── OrderNotFoundException.java
        │   ├── OrderCancellationException.java
        │   └── InvalidOrderStatusException.java
        ├── repository/
        │   └── OrderRepository.java
        ├── resource/
        │   └── OrderResource.java
        └── service/
            ├── OrderService.java
            └── OrderServiceImpl.java
```

#### 2. 응답 예시

**성공 응답 - 주문 조회 (200 OK)**
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "orderNumber": "ORD-20260205-0001",
    "customerId": "c1234567-1234-1234-1234-123456789012",
    "status": "CREATED",
    "totalAmount": 150000.00,
    "items": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "productCode": "PROD-001",
        "quantity": 5,
        "unitPrice": 30000.00,
        "subtotal": 150000.00
      }
    ],
    "createdAt": "2026-02-05T10:30:00",
    "updatedAt": "2026-02-05T10:30:00"
  },
  "timestamp": "2026-02-05T14:30:00"
}
```

**성공 응답 - 주문 생성 (201 Created)**
```json
{
  "success": true,
  "data": { ... },
  "message": "주문이 성공적으로 생성되었습니다.",
  "timestamp": "2026-02-05T14:30:00"
}
```

**에러 응답 - 주문 없음 (404 Not Found)**
```json
{
  "code": "O001",
  "message": "주문을 찾을 수 없습니다.",
  "status": 404,
  "timestamp": "2026-02-05T14:30:00",
  "path": "/api/v1/orders/550e8400-e29b-41d4-a716-446655440000"
}
```

**에러 응답 - Validation 실패 (400 Bad Request)**
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
      "value": "null",
      "reason": "고객 ID는 필수입니다."
    },
    {
      "field": "items",
      "value": "[]",
      "reason": "주문 항목은 최소 1개 이상이어야 합니다."
    }
  ]
}
```

---

## 체크리스트

새 서비스에 공통 응답 규격 및 예외 처리를 적용할 때 다음 체크리스트를 확인하세요.

### 필수 구현 항목

#### 패키지 구조
- [ ] `common.exception` 패키지 생성
- [ ] `domain.{domain}.dto.request` 패키지 생성
- [ ] `domain.{domain}.dto.response` 패키지 생성
- [ ] `domain.{domain}.exception` 패키지 생성

#### 공통 예외 인프라
- [ ] `BaseException` 작성
- [ ] `BusinessException` 작성
- [ ] `EntityNotFoundException` 작성
- [ ] `InvalidInputException` 작성
- [ ] `DuplicateEntityException` 작성
- [ ] `UnauthorizedException` 작성
- [ ] `ErrorCode` enum 작성 (공통 + 서비스별)
- [ ] `GlobalExceptionHandler` 작성

#### 공통 응답 DTO
- [ ] `ApiResponse<T>` 작성 (response 패키지)
- [ ] `ErrorResponse` 작성 (response 패키지)
- [ ] 도메인 응답 DTO 작성 (response 패키지)
- [ ] 도메인 요청 DTO 작성 (request 패키지)

#### 도메인 예외
- [ ] 도메인 예외 클래스 작성 (`domain.{domain}.exception` 패키지)

### 검증 항목

- [ ] 모든 예외가 적절한 상위 클래스를 상속하는가?
- [ ] ErrorCode에 서비스별 prefix가 올바르게 적용되었는가?
- [ ] GlobalExceptionHandler가 모든 예외를 처리하는가?
- [ ] ApiResponse와 ErrorResponse 형식이 일관되는가?
- [ ] Resource의 모든 엔드포인트가 ApiResponse<T>를 반환하는가?
- [ ] 로그가 적절하게 남는가?
- [ ] HTTP 상태 코드가 올바른가?
- [ ] DTO 패키지 분리가 명확한가? (request/response)

---

## 참고 자료

### 실제 구현체
- **Order Service**: `C:\study\git\msa-scm\oms\order-service`
- **Inventory Service**: `C:\study\git\msa-scm\oms\inventory-service`

### 관련 문서
- [CODING_CONVENTION.md](./CODING_CONVENTION.md)
- [Spring Boot Exception Handling Best Practices](https://www.baeldung.com/exception-handling-for-rest-with-spring)
- [RFC 7807: Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc7807)

---

**작성일**: 2026-02-05  
**버전**: 2.0  
**작성자**: System  
**변경 이력**:
- 2.0 (2026-02-05): ApiResponse 공통 응답 규격 추가, 패키지 구조 정리
- 1.0 (2026-02-05): 초기 예외 처리 가이드 작성
