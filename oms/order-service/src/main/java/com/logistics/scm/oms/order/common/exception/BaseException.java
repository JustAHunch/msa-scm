package com.logistics.scm.oms.order.common.exception;

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
