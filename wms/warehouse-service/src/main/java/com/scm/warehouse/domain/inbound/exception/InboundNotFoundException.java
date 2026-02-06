package com.scm.warehouse.domain.inbound.exception;

import java.util.UUID;

/**
 * 입고 주문을 찾을 수 없을 때 발생하는 예외
 * 
 * @author c.h.jo
 * @since 2026-02-06
 */
public class InboundNotFoundException extends RuntimeException {

    public InboundNotFoundException(UUID inboundId) {
        super("입고 주문을 찾을 수 없습니다: " + inboundId);
    }

    public InboundNotFoundException(String inboundNumber) {
        super("입고 주문을 찾을 수 없습니다: " + inboundNumber);
    }

    public InboundNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
