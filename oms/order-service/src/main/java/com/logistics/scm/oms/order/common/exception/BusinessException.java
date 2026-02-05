package com.logistics.scm.oms.order.common.exception;

/**
 * 비즈니스 로직 예외
 * 일반적인 비즈니스 규칙 위반 시 발생
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
