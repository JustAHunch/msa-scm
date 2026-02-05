package com.logistics.scm.oms.inventory.common.exception;

/**
 * 인증/인가 실패 예외
 * 인증되지 않았거나 권한이 없을 때 발생
 *
 * @author c.h.jo
 * @since 2025-02-05
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}