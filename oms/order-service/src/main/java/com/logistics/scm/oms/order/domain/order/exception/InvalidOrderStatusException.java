package com.logistics.scm.oms.order.domain.order.exception;

import com.logistics.scm.oms.order.common.exception.InvalidInputException;
import com.logistics.scm.oms.order.common.exception.ErrorCode;
import com.logistics.scm.oms.order.domain.order.entity.Order;
import lombok.Getter;

import java.util.UUID;

/**
 * 유효하지 않은 주문 상태 예외
 * 주문 상태가 요청된 작업을 수행할 수 없는 상태일 때 발생
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
public class InvalidOrderStatusException extends InvalidInputException {

    private final UUID orderId;
    private final Order.OrderStatus currentStatus;
    private final String attemptedAction;

    public InvalidOrderStatusException(UUID orderId, Order.OrderStatus currentStatus, String attemptedAction) {
        super(ErrorCode.INVALID_ORDER_STATUS);
        this.orderId = orderId;
        this.currentStatus = currentStatus;
        this.attemptedAction = attemptedAction;
    }

    @Override
    public String getMessage() {
        return String.format("%s (주문ID: %s, 현재상태: %s, 시도작업: %s)",
                super.getMessage(), orderId, currentStatus, attemptedAction);
    }
}
