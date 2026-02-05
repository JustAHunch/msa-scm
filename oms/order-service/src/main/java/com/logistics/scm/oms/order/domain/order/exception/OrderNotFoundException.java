package com.logistics.scm.oms.order.domain.order.exception;

import com.logistics.scm.oms.order.common.exception.EntityNotFoundException;
import com.logistics.scm.oms.order.common.exception.ErrorCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 주문 조회 실패 예외
 * 요청한 주문을 찾을 수 없을 때 발생
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
public class OrderNotFoundException extends EntityNotFoundException {

    private final UUID orderId;

    public OrderNotFoundException(UUID orderId) {
        super(ErrorCode.ORDER_NOT_FOUND);
        this.orderId = orderId;
    }

    @Override
    public String getMessage() {
        return String.format("%s (주문ID: %s)", super.getMessage(), orderId);
    }
}
