package com.scm.warehouse.domain.inbound.exception;

/**
 * 잘못된 입고 상태일 때 발생하는 예외
 * 
 * @author c.h.jo
 * @since 2026-02-06
 */
public class InvalidInboundStateException extends RuntimeException {

    public InvalidInboundStateException(String message) {
        super(message);
    }

    public InvalidInboundStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
