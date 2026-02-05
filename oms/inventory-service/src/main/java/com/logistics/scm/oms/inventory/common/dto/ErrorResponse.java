package com.logistics.scm.oms.inventory.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 에러 응답 DTO
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Getter
@Builder
public class ErrorResponse {

    private String code;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
    private List<FieldError> errors;

    @Getter
    @Builder
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
    }
}
