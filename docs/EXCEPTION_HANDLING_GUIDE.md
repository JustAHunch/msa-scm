# MSA 서비스 예외 처리 가이드

> 모든 서비스에 일관된 예외 처리 체계를 적용하기 위한 가이드

## 📋 목차
- [개요](#개요)
- [패키지 구조](#패키지-구조)
- [구현 단계](#구현-단계)
- [ErrorCode 설계 규칙](#errorcode-설계-규칙)
- [예외 클래스 작성 가이드](#예외-클래스-작성-가이드)
- [서비스별 적용 예시](#서비스별-적용-예시)

---

## 개요

### 목적
- **일관성**: 모든 서비스에서 동일한 에러 응답 형식 제공
- **추적성**: 에러 코드를 통한 빠른 문제 파악
- **유지보수성**: 중앙 집중식 예외 관리

### 핵심 원칙
1. **패키지 분리**: 공통 예외(common.exception) vs 도메인 예외(exception)
2. **계층 구조**: BaseException → 특화 예외 → 도메인 예외
3. **에러 코드 체계**: 서비스별 prefix + 일련번호

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
├── dto/
│   └── ErrorResponse.java      # 에러 응답 DTO
└── exception/                  # 도메인 특화 예외
    ├── {Domain}NotFoundException.java
    └── {Domain}BusinessException.java
```

### 패키지별 역할

| 패키지 | 역할 | 예시 |
|--------|------|------|
| `common.exception` | 공통 예외 인프라, 전역 핸들러 | BaseException, ErrorCode |
| `dto` | 에러 응답 DTO | ErrorResponse |
| `exception` | 도메인 특화 예외 | InsufficientStockException |

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
 * @since 2025-02-05
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
 * @since 2025-02-05
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

### Step 3: ErrorResponse DTO 생성

```java
package com.logistics.scm.{domain}.{service}.dto;

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
 * @since 2025-02-05
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

### Step 4: GlobalExceptionHandler 생성

```java
package com.logistics.scm.{domain}.{service}.common.exception;

import com.logistics.scm.{domain}.{service}.dto.ErrorResponse;
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
 * @since 2025-02-05
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
     * BindException 처리
     * @ModelAttribute 바인딩 실패 시 발생
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(
            BindException e, HttpServletRequest request) {

        log.error("Bind Error: {}", e.getMessage());

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
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * MissingServletRequestParameterException 처리
     * 필수 요청 파라미터 누락 시 발생
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {

        log.error("Missing Parameter: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT_VALUE.getCode())
                .message("필수 파라미터가 누락되었습니다: " + e.getParameterName())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * MethodArgumentTypeMismatchException 처리
     * 파라미터 타입 불일치 시 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        log.error("Type Mismatch: {}", e.getMessage());

        String message = String.format("'%s' 파라미터의 값 '%s'은(는) '%s' 타입이어야 합니다.",
                e.getName(),
                e.getValue(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");

        ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.INVALID_INPUT_VALUE.getCode())
                .message(message)
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

### Step 5: 도메인 예외 작성

도메인별 특화 예외는 `exception` 패키지에 작성합니다.

**예시: InsufficientStockException**
```java
package com.logistics.scm.{domain}.{service}.exception;

import com.logistics.scm.{domain}.{service}.common.exception.BusinessException;
import com.logistics.scm.{domain}.{service}.common.exception.ErrorCode;
import lombok.Getter;

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
        return String.format("%s (상품코드: %s, 요청수량: %d, 가용재고: %d)",
                super.getMessage(), productCode, requestedQuantity, availableQuantity);
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

### 에러 코드 작성 가이드

#### 1. NOT_FOUND 에러 (404)
```java
{ENTITY}_NOT_FOUND(HttpStatus.NOT_FOUND, "{PREFIX}001", "{엔티티}를 찾을 수 없습니다.")

// 예시
INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "재고를 찾을 수 없습니다."),
ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "창고를 찾을 수 없습니다."),
```

#### 2. BAD_REQUEST 에러 (400)
```java
INVALID_{DOMAIN}_{FIELD}(HttpStatus.BAD_REQUEST, "{PREFIX}00X", "유효하지 않은 {필드}입니다.")

// 예시
INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "I005", "유효하지 않은 재고 수량입니다."),
INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O005", "유효하지 않은 주문 상태입니다."),
```

#### 3. CONFLICT 에러 (409)
```java
{ENTITY}_ALREADY_EXISTS(HttpStatus.CONFLICT, "{PREFIX}01X", "이미 존재하는 {엔티티}입니다.")

// 예시
INVENTORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "I010", "이미 존재하는 재고입니다."),
ORDER_ALREADY_EXISTS(HttpStatus.CONFLICT, "O010", "이미 존재하는 주문입니다."),
```

#### 4. 비즈니스 로직 에러
```java
{ACTION}_FAILED(HttpStatus.BAD_REQUEST, "{PREFIX}02X", "{작업}에 실패했습니다.")

// 예시
STOCK_RESERVATION_FAILED(HttpStatus.BAD_REQUEST, "I006", "재고 예약에 실패했습니다."),
ORDER_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "O020", "주문 취소에 실패했습니다."),
DELIVERY_SCHEDULE_FAILED(HttpStatus.BAD_REQUEST, "D020", "배송 일정 수립에 실패했습니다."),
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
package com.logistics.scm.{domain}.{service}.exception;

import com.logistics.scm.{domain}.{service}.common.exception.{부모Exception};
import com.logistics.scm.{domain}.{service}.common.exception.ErrorCode;
import lombok.Getter;

/**
 * {예외 설명}
 * {발생 조건}
 *
 * @author c.h.jo
 * @since 2025-02-05
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

### 1. Order Service (주문 서비스)

#### ErrorCode 정의
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

#### 도메인 예외 예시
```java
// OrderNotFoundException.java
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

// OrderCancellationException.java
@Getter
public class OrderCancellationException extends BusinessException {
    private final UUID orderId;
    private final String reason;

    public OrderCancellationException(UUID orderId, String reason) {
        super(ErrorCode.ORDER_CANCEL_FAILED);
        this.orderId = orderId;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return String.format("%s (주문ID: %s, 사유: %s)", 
                super.getMessage(), orderId, reason);
    }
}
```

---

### 2. Warehouse Service (창고 서비스)

#### ErrorCode 정의
```java
// 창고 관련 (W001~W999)
WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "W001", "창고를 찾을 수 없습니다."),
LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "W002", "로케이션을 찾을 수 없습니다."),
WORKER_NOT_FOUND(HttpStatus.NOT_FOUND, "W003", "작업자를 찾을 수 없습니다."),
PICKING_FAILED(HttpStatus.BAD_REQUEST, "W004", "피킹 작업에 실패했습니다."),
PACKING_FAILED(HttpStatus.BAD_REQUEST, "W005", "패킹 작업에 실패했습니다."),
INVALID_WAREHOUSE_ZONE(HttpStatus.BAD_REQUEST, "W006", "유효하지 않은 창고 구역입니다."),
WAREHOUSE_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "W007", "창고 용량을 초과했습니다."),
```

#### 도메인 예외 예시
```java
// PickingFailureException.java
@Getter
public class PickingFailureException extends BusinessException {
    private final UUID taskId;
    private final String productCode;
    private final String reason;

    public PickingFailureException(UUID taskId, String productCode, String reason) {
        super(ErrorCode.PICKING_FAILED);
        this.taskId = taskId;
        this.productCode = productCode;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return String.format("%s (작업ID: %s, 상품: %s, 사유: %s)",
                super.getMessage(), taskId, productCode, reason);
    }
}
```

---

### 3. Delivery Service (배송 서비스)

#### ErrorCode 정의
```java
// 배송 관련 (D001~D999)
DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "배송을 찾을 수 없습니다."),
VEHICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "D002", "차량을 찾을 수 없습니다."),
DRIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "D003", "배송기사를 찾을 수 없습니다."),
ROUTE_OPTIMIZATION_FAILED(HttpStatus.BAD_REQUEST, "D004", "경로 최적화에 실패했습니다."),
DELIVERY_SCHEDULE_FAILED(HttpStatus.BAD_REQUEST, "D005", "배송 일정 수립에 실패했습니다."),
VEHICLE_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "D006", "차량 적재 용량을 초과했습니다."),
INVALID_DELIVERY_STATUS(HttpStatus.BAD_REQUEST, "D007", "유효하지 않은 배송 상태입니다."),
DELIVERY_ALREADY_COMPLETED(HttpStatus.CONFLICT, "D008", "이미 완료된 배송입니다."),
```

#### 도메인 예외 예시
```java
// VehicleCapacityException.java
@Getter
public class VehicleCapacityException extends BusinessException {
    private final UUID vehicleId;
    private final Integer currentLoad;
    private final Integer maxCapacity;

    public VehicleCapacityException(UUID vehicleId, Integer currentLoad, Integer maxCapacity) {
        super(ErrorCode.VEHICLE_CAPACITY_EXCEEDED);
        this.vehicleId = vehicleId;
        this.currentLoad = currentLoad;
        this.maxCapacity = maxCapacity;
    }

    @Override
    public String getMessage() {
        return String.format("%s (차량ID: %s, 현재: %d, 최대: %d)",
                super.getMessage(), vehicleId, currentLoad, maxCapacity);
    }
}
```

---

## 사용 예시

### Service Layer에서 예외 발생

```java
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public void reserveStock(UUID warehouseId, String productCode, Integer quantity) {
        // 1. 재고 조회
        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductCode(warehouseId, productCode)
                .orElseThrow(() -> new InventoryNotFoundException(warehouseId, productCode));

        // 2. 재고 부족 체크
        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(
                    productCode, 
                    quantity, 
                    inventory.getAvailableQuantity()
            );
        }

        // 3. 재고 예약
        inventory.reserve(quantity);
    }
}
```

### Controller에서 자동 처리

```java
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@RequestBody @Valid ReserveStockRequestDTO request) {
        // 예외는 GlobalExceptionHandler가 자동 처리
        inventoryService.reserveStock(
                request.getWarehouseId(), 
                request.getProductCode(), 
                request.getQuantity()
        );
        return ResponseEntity.ok().build();
    }
}
```

### 에러 응답 예시

**성공 응답 (200 OK)**
```json
{
  "data": { ... }
}
```

**재고 부족 에러 (400 Bad Request)**
```json
{
  "code": "I002",
  "message": "재고가 부족합니다.",
  "status": 400,
  "timestamp": "2025-02-05T14:30:00",
  "path": "/api/inventory/reserve"
}
```

**재고 없음 에러 (404 Not Found)**
```json
{
  "code": "I001",
  "message": "재고를 찾을 수 없습니다.",
  "status": 404,
  "timestamp": "2025-02-05T14:30:00",
  "path": "/api/inventory/reserve"
}
```

**Validation 에러 (400 Bad Request)**
```json
{
  "code": "C002",
  "message": "잘못된 입력값입니다.",
  "status": 400,
  "timestamp": "2025-02-05T14:30:00",
  "path": "/api/inventory/reserve",
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

## 체크리스트

새 서비스에 예외 처리를 적용할 때 다음 체크리스트를 확인하세요.

### 필수 구현 항목

- [ ] `common.exception` 패키지 생성
- [ ] `BaseException` 작성
- [ ] `BusinessException` 작성
- [ ] `EntityNotFoundException` 작성
- [ ] `InvalidInputException` 작성
- [ ] `DuplicateEntityException` 작성
- [ ] `UnauthorizedException` 작성
- [ ] `ErrorCode` enum 작성 (공통 + 서비스별)
- [ ] `GlobalExceptionHandler` 작성
- [ ] `dto/ErrorResponse` 작성
- [ ] 도메인 예외 클래스 작성 (`exception` 패키지)

### 검증 항목

- [ ] 모든 예외가 적절한 상위 클래스를 상속하는가?
- [ ] ErrorCode에 서비스별 prefix가 올바르게 적용되었는가?
- [ ] GlobalExceptionHandler가 모든 예외를 처리하는가?
- [ ] ErrorResponse 형식이 일관되는가?
- [ ] 로그가 적절하게 남는가?
- [ ] HTTP 상태 코드가 올바른가?

---

## 참고 자료

### Inventory Service 구현체
- 위치: `C:\study\git\msa-scm\oms\inventory-service\src\main\java\com\logistics\scm\oms\inventory`
- 실제 구현 코드를 참고하여 다른 서비스에 적용

### 관련 문서
- [Spring Boot Exception Handling Best Practices](https://www.baeldung.com/exception-handling-for-rest-with-spring)
- [RFC 7807: Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc7807)

---

**작성일**: 2025-02-05  
**버전**: 1.0  
**작성자**: System
