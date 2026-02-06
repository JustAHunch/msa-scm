package com.logistics.scm.oms.inventory.domain.inbound.resource;

import com.logistics.scm.oms.inventory.common.dto.ApiResponse;
import com.logistics.scm.oms.inventory.common.dto.ErrorResponse;
import com.logistics.scm.oms.inventory.domain.inbound.dto.request.InboundCreateRequest;
import com.logistics.scm.oms.inventory.domain.inbound.dto.request.InboundInspectionRequest;
import com.logistics.scm.oms.inventory.domain.inbound.dto.response.InboundResponse;
import com.logistics.scm.oms.inventory.domain.inbound.entity.InboundStatus;
import com.logistics.scm.oms.inventory.domain.inbound.service.InboundService;
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

import java.util.List;
import java.util.UUID;

/**
 * 입고 API Resource
 *
 * SERVICE_FLOW "2.2 허브 입고" 프로세스 API
 * - 입고 신청, 검수 시작, 검수 완료(합격/불합격), 조회
 *
 * @author c.h.jo
 * @since 2026-02-06
 */
@Tag(name = "허브 입고", description = "허브 입고 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/inbounds")
@RequiredArgsConstructor
public class InboundResource {

    private final InboundService inboundService;

    /**
     * 입고 신청
     * POST /api/v1/inbounds
     */
    @Operation(
        summary = "입고 신청",
        description = "상품을 특정 허브에 입고 신청합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "신청 성공",
            content = @Content(schema = @Schema(implementation = InboundResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<InboundResponse>> createInbound(
            @Parameter(description = "입고 신청 요청", required = true)
            @Valid @RequestBody InboundCreateRequest request) {
        log.info("입고 신청 요청: productCode={}, warehouseId={}",
                request.getProductCode(), request.getWarehouseId());

        InboundResponse response = inboundService.createInbound(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "입고 신청이 완료되었습니다."));
    }

    /**
     * 입고 조회
     * GET /api/v1/inbounds/{id}
     */
    @Operation(
        summary = "입고 조회",
        description = "ID로 입고 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InboundResponse>> getInbound(
            @Parameter(description = "입고 ID (UUID)", required = true)
            @PathVariable UUID id) {
        log.info("입고 조회: id={}", id);

        InboundResponse response = inboundService.getInboundById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 검수 시작
     * POST /api/v1/inbounds/{id}/inspect/start?inspectorId={}
     */
    @Operation(
        summary = "검수 시작",
        description = "허브 관리자가 입고 검수를 시작합니다."
    )
    @PostMapping("/{id}/inspect/start")
    public ResponseEntity<ApiResponse<InboundResponse>> startInspection(
            @Parameter(description = "입고 ID (UUID)", required = true)
            @PathVariable UUID id,
            @Parameter(description = "검수자 ID", required = true)
            @RequestParam String inspectorId) {
        log.info("검수 시작: id={}, inspectorId={}", id, inspectorId);

        InboundResponse response = inboundService.startInspection(id, inspectorId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "검수가 시작되었습니다.")
        );
    }

    /**
     * 검수 완료 (합격/불합격)
     * POST /api/v1/inbounds/{id}/inspect/complete
     */
    @Operation(
        summary = "검수 완료",
        description = "검수 결과를 처리합니다. 합격 시 재고가 등록되고, 불합격 시 반송 처리됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "검수 완료",
            content = @Content(schema = @Schema(implementation = InboundResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 상태 전이",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "입고 정보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/{id}/inspect/complete")
    public ResponseEntity<ApiResponse<InboundResponse>> completeInspection(
            @Parameter(description = "입고 ID (UUID)", required = true)
            @PathVariable UUID id,
            @Parameter(description = "검수 결과 요청", required = true)
            @Valid @RequestBody InboundInspectionRequest request) {
        log.info("검수 완료: id={}, approved={}", id, request.getApproved());

        InboundResponse response = inboundService.completeInspection(id, request);

        String message = request.getApproved()
                ? "입고 검수 합격 처리 완료. 재고가 등록되었습니다."
                : "입고 검수 불합격 처리 완료. 반송 처리됩니다.";

        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    /**
     * 상태별 입고 목록 조회
     * GET /api/v1/inbounds/status/{status}
     */
    @Operation(
        summary = "상태별 입고 조회",
        description = "입고 상태별로 목록을 조회합니다."
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<InboundResponse>>> getInboundsByStatus(
            @Parameter(description = "입고 상태 (REQUESTED, INSPECTING, APPROVED, REJECTED, COMPLETED)", required = true)
            @PathVariable InboundStatus status) {
        log.info("상태별 입고 조회: status={}", status);

        List<InboundResponse> responses = inboundService.getInboundsByStatus(status);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 창고별 입고 목록 조회
     * GET /api/v1/inbounds/warehouse/{warehouseId}
     */
    @Operation(
        summary = "창고별 입고 조회",
        description = "창고 ID로 해당 창고의 입고 목록을 조회합니다."
    )
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<InboundResponse>>> getInboundsByWarehouse(
            @Parameter(description = "창고 ID (UUID)", required = true)
            @PathVariable UUID warehouseId) {
        log.info("창고별 입고 조회: warehouseId={}", warehouseId);

        List<InboundResponse> responses = inboundService.getInboundsByWarehouse(warehouseId);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
