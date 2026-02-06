package com.logistics.scm.oms.inventory.domain.inbound.exception;

import com.logistics.scm.oms.inventory.common.exception.EntityNotFoundException;
import com.logistics.scm.oms.inventory.common.exception.ErrorCode;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InboundNotFoundException extends EntityNotFoundException {
    private final String identifier;

    public InboundNotFoundException(UUID inboundId) {
        super(ErrorCode.ENTITY_NOT_FOUND);
        this.identifier = inboundId.toString();
    }

    @Override
    public String getMessage() {
        return String.format("입고 정보를 찾을 수 없습니다. (식별자: %s)", identifier);
    }
}
