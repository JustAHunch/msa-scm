package com.logistics.scm.oms.inventory.domain.inventory.resource;

import com.logistics.scm.oms.inventory.common.dto.ApiResponse;
import com.logistics.scm.oms.inventory.common.dto.ErrorResponse;
import com.logistics.scm.oms.inventory.domain.inventory.dto.request.CreateInventoryRequest;
import com.logistics.scm.oms.inventory.domain.inventory.dto.request.ReleaseStockRequest;
import com.logistics.scm.oms.inventory.domain.inventory.dto.request.ReserveStockRequest;
import com.logistics.scm.oms.inventory.domain.inventory.dto.response.InventoryResponse;
import com.logistics.scm.oms.inventory.domain.inventory.service.InventoryService;
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
 * 재고 API Resource
 *
 * 재고 조회, 생성/업데이트, 예약, 해제 API 제공
 *
 * @author c.h.jo
 * @since 2026-02-06
 */
@Tag(name = "재고", description = "재고 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryResource {

    private final InventoryService inventoryService;

    /**
     * 재고 조회 (창고ID + 상품코드)
     * GET /api/v1/inventories?warehouseId={}&productCode={}
     */
    @Operation(
        summary = "재고 조회",
        description = "창고 ID와 상품 코드로 재고를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = InventoryResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "재고를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(
            @Parameter(description = "창고 ID (UUID)", required = true)
            @RequestParam UUID warehouseId,
            @Parameter(description = "상품 코드", required = true)
            @RequestParam String productCode) {
        log.info("재고 조회: warehouseId={}, productCode={}", warehouseId, productCode);

        InventoryResponse response = inventoryService.getInventory(warehouseId, productCode);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 상품코드로 모든 창고의 재고 조회
     * GET /api/v1/inventories/product/{productCode}
     */
    @Operation(
        summary = "상품별 재고 조회",
        description = "상품 코드로 모든 창고의 재고를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        )
    })
    @GetMapping("/product/{productCode}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoriesByProduct(
            @Parameter(description = "상품 코드", required = true)
            @PathVariable String productCode) {
        log.info("상품별 재고 조회: productCode={}", productCode);

        List<InventoryResponse> responses = inventoryService.getInventoriesByProductCode(productCode);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 창고별 모든 재고 조회
     * GET /api/v1/inventories/warehouse/{warehouseId}
     */
    @Operation(
        summary = "창고별 재고 조회",
        description = "창고 ID로 해당 창고의 모든 재고를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        )
    })
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoriesByWarehouse(
            @Parameter(description = "창고 ID (UUID)", required = true)
            @PathVariable UUID warehouseId) {
        log.info("창고별 재고 조회: warehouseId={}", warehouseId);

        List<InventoryResponse> responses = inventoryService.getInventoriesByWarehouse(warehouseId);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 재고 부족 상품 목록 조회
     * GET /api/v1/inventories/low-stock
     */
    @Operation(
        summary = "재고 부족 상품 조회",
        description = "안전 재고 미만인 상품 목록을 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        )
    })
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStockInventories() {
        log.info("재고 부족 상품 조회");

        List<InventoryResponse> responses = inventoryService.getLowStockInventories();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 재고 확인 (주문 가능 여부)
     * GET /api/v1/inventories/check?warehouseId={}&productCode={}&quantity={}
     */
    @Operation(
        summary = "재고 확인",
        description = "주문 수량에 대한 재고 가용성을 확인합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "확인 성공"
        )
    })
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkStock(
            @Parameter(description = "창고 ID (UUID)", required = true)
            @RequestParam UUID warehouseId,
            @Parameter(description = "상품 코드", required = true)
            @RequestParam String productCode,
            @Parameter(description = "요청 수량", required = true)
            @RequestParam Integer quantity) {
        log.info("재고 확인: warehouseId={}, productCode={}, quantity={}",
                warehouseId, productCode, quantity);

        boolean isAvailable = inventoryService.checkStock(warehouseId, productCode, quantity);

        return ResponseEntity.ok(ApiResponse.success(isAvailable));
    }

    /**
     * 재고 생성 또는 업데이트 (입고)
     * POST /api/v1/inventories
     */
    @Operation(
        summary = "재고 생성/업데이트",
        description = "재고가 없으면 신규 생성하고, 있으면 수량을 증가시킵니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "생성/업데이트 성공",
            content = @Content(schema = @Schema(implementation = InventoryResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> createOrUpdateInventory(
            @Parameter(description = "재고 생성/업데이트 요청", required = true)
            @Valid @RequestBody CreateInventoryRequest request) {
        log.info("재고 생성/업데이트: warehouseId={}, productCode={}, quantity={}",
                request.getWarehouseId(), request.getProductCode(), request.getQuantity());

        InventoryResponse response = inventoryService.createOrUpdateInventory(
                request.getWarehouseId(),
                request.getProductCode(),
                request.getQuantity(),
                request.getSafetyStock()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "재고가 성공적으로 생성/업데이트되었습니다."));
    }

    /**
     * 재고 예약 (수동)
     * POST /api/v1/inventories/reserve
     */
    @Operation(
        summary = "재고 예약",
        description = "수동으로 재고를 예약합니다. 가용수량을 감소시키고 할당수량을 증가시킵니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "예약 성공",
            content = @Content(schema = @Schema(implementation = InventoryResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "재고 부족",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "재고를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<InventoryResponse>> reserveStock(
            @Parameter(description = "재고 예약 요청", required = true)
            @Valid @RequestBody ReserveStockRequest request) {
        log.info("재고 예약(수동): warehouseId={}, productCode={}, quantity={}",
                request.getWarehouseId(), request.getProductCode(), request.getQuantity());

        InventoryResponse response = inventoryService.reserveStock(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "재고가 성공적으로 예약되었습니다.")
        );
    }

    /**
     * 재고 해제 (수동)
     * POST /api/v1/inventories/release
     */
    @Operation(
        summary = "재고 해제",
        description = "수동으로 예약된 재고를 해제합니다. 할당수량을 감소시키고 가용수량을 증가시킵니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "해제 성공",
            content = @Content(schema = @Schema(implementation = InventoryResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "재고를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/release")
    public ResponseEntity<ApiResponse<InventoryResponse>> releaseStock(
            @Parameter(description = "재고 해제 요청", required = true)
            @Valid @RequestBody ReleaseStockRequest request) {
        log.info("재고 해제(수동): warehouseId={}, productCode={}, quantity={}",
                request.getWarehouseId(), request.getProductCode(), request.getQuantity());

        InventoryResponse response = inventoryService.releaseStock(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "재고가 성공적으로 해제되었습니다.")
        );
    }
}
