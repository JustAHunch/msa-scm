package com.logistics.scm.oms.order.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 Enum
 * Order Service 전체 에러 코드 중앙 관리
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

    // 주문 관련 (O001~O999)
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "O002", "고객을 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O003", "유효하지 않은 주문 상태입니다."),
    ORDER_ALREADY_CANCELLED(HttpStatus.CONFLICT, "O004", "이미 취소된 주문입니다."),
    ORDER_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "O005", "주문 취소에 실패했습니다."),
    INSUFFICIENT_PAYMENT(HttpStatus.BAD_REQUEST, "O006", "결제 금액이 부족합니다."),
    ORDER_ALREADY_COMPLETED(HttpStatus.CONFLICT, "O007", "이미 완료된 주문입니다."),
    ORDER_CREATE_FAILED(HttpStatus.BAD_REQUEST, "O008", "주문 생성에 실패했습니다."),
    ORDER_CONFIRM_FAILED(HttpStatus.BAD_REQUEST, "O009", "주문 확정에 실패했습니다."),
    INVALID_ORDER_ITEM(HttpStatus.BAD_REQUEST, "O010", "유효하지 않은 주문 항목입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
