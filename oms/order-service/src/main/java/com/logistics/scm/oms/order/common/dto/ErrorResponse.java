package com.logistics.scm.oms.order.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 에러 응답 DTO
 * 일관된 에러 응답 형식 제공
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String code;
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;
    private final String path;
    private final List<FieldError> errors;

    /**
     * 필드 에러 정보
     */
    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;
    }
}
