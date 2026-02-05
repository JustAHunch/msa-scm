package com.logistics.scm.oms.order.domain.order.resource;

import com.logistics.scm.oms.order.common.dto.ApiResponse;
import com.logistics.scm.oms.order.common.dto.ErrorResponse;
import com.logistics.scm.oms.order.domain.order.dto.request.OrderCancelRequest;
import com.logistics.scm.oms.order.domain.order.dto.request.OrderCreateRequest;
import com.logistics.scm.oms.order.domain.order.dto.response.OrderResponse;
import com.logistics.scm.oms.order.domain.order.entity.Order;
import com.logistics.scm.oms.order.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        description = "ID로 특정 주문을 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "주문을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> load(
            @Parameter(description = "주문 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        log.info("주문 조회: id={}", id);

        Order order = orderService.loadOrderById(id);
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    /**
     * 주문 생성
     * POST /api/v1/orders
     */
    @Operation(
        summary = "주문 생성",
        description = "새로운 주문을 생성합니다. 주문 생성 시 OrderCreatedEvent가 Kafka로 발행됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "생성 성공",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @Parameter(description = "주문 생성 요청", required = true)
            @Valid @RequestBody OrderCreateRequest request) {
        log.info("주문 생성 요청: customerId={}, itemCount={}",
                request.getCustomerId(), request.getItems().size());

        // Request를 Entity로 변환
        Order order = Order.from(request);

        // 주문 생성 (Kafka 이벤트 발행 포함)
        Order createdOrder = orderService.createOrder(order);

        OrderResponse response = OrderResponse.from(createdOrder);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        response, "주문이 성공적으로 생성되었습니다."
                ));
    }

    /**
     * 주문 취소 (사용자 요청)
     * DELETE /api/v1/orders/{id}
     */
    @Operation(
        summary = "주문 취소",
        description = "사용자가 주문을 취소합니다. OrderCancelledEvent가 Kafka로 발행되어 재고가 원복됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "취소 성공",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "주문을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "취소할 수 없는 상태",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(
            @Parameter(description = "주문 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @Parameter(description = "취소 사유", required = true)
            @Valid @RequestBody OrderCancelRequest request) {
        log.info("주문 취소 요청: id={}, reason={}", id, request.getCancelReason());

        Order cancelledOrder = orderService.cancelOrder(id, request.getCancelReason());
        OrderResponse response = OrderResponse.from(cancelledOrder);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response, "주문이 성공적으로 취소되었습니다."
                )
        );
    }
}
