package com.logistics.scm.oms.inventory.common.exception;

/**
 * 중복 엔티티 예외
 * 이미 존재하는 데이터를 생성하려고 할 때 발생
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
public class DuplicateEntityException extends BaseException {

    public DuplicateEntityException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateEntityException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}