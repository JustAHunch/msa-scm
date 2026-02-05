package com.logistics.scm.oms.order.domain.order.exception;

import com.logistics.scm.oms.order.common.exception.BusinessException;
import com.logistics.scm.oms.order.common.exception.ErrorCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 주문 취소 실패 예외
 * 주문 취소 시 비즈니스 규칙 위반이 발생했을 때 발생
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
public class OrderCancellationException extends BusinessException {

    private final UUID orderId;
    private final String reason;

    public OrderCancellationException(UUID orderId, String reason) {
        super(ErrorCode.ORDER_CANCEL_FAILED);
        this.orderId = orderId;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return String.format("%s (주문ID: %s, 사유: %s)",
                super.getMessage(), orderId, reason);
    }
}
