package com.logistics.scm.oms.order.domain.order.resource;

import com.logistics.scm.oms.order.domain.order.dto.OrderCreateRequest;
import com.logistics.scm.oms.order.domain.order.dto.OrderResponse;
import com.logistics.scm.oms.order.domain.order.entity.Order;
import com.logistics.scm.oms.order.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 주문 API Resource
 *
 * @author c.h.jo
 * @since 2026-02-05
 */
@Tag(name = "주문", description = "주문 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderResource {

    private final OrderService orderService;

    /**
     * 특정 주문 조회
     * GET /api/v1/orders/{id}
     */
    @Operation(
        summary = "주문 조회",
        description = "ID로 특정 주문를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "주문를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> load(
            @Parameter(description = "주문 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        log.info("주문 조회: id={}", id);
    }
}
