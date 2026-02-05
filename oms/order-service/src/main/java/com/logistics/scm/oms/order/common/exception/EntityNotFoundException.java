package com.logistics.scm.oms.order.common.exception;

/**
 * 엔티티 조회 실패 예외
 * 요청한 리소스를 찾을 수 없을 때 발생
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
public class EntityNotFoundException extends BaseException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityNotFoundException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
