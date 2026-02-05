package com.logistics.scm.oms.inventory.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

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

    // 재고 관련 (I001~I999)
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "재고를 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "I002", "재고가 부족합니다."),
    STOCK_ALREADY_RESERVED(HttpStatus.CONFLICT, "I003", "이미 예약된 재고입니다."),
    STOCK_MOVEMENT_FAILED(HttpStatus.BAD_REQUEST, "I004", "재고 이동에 실패했습니다."),
    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "I005", "유효하지 않은 재고 수량입니다."),
    STOCK_RESERVATION_FAILED(HttpStatus.BAD_REQUEST, "I006", "재고 예약에 실패했습니다."),
    STOCK_RELEASE_FAILED(HttpStatus.BAD_REQUEST, "I007", "재고 해제에 실패했습니다."),
    WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "I008", "창고를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "I009", "상품을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}