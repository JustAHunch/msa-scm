package com.logistics.scm.oms.inventory.common.exception;

/**
 * 잘못된 입력값 예외
 * 유효하지 않은 요청 데이터가 전달되었을 때 발생
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
public class InvalidInputException extends BaseException {

    public InvalidInputException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidInputException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
