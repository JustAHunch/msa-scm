package com.logistics.scm.oms.inventory.domain.product.resource;

import com.logistics.scm.oms.inventory.common.dto.ApiResponse;
import com.logistics.scm.oms.inventory.common.dto.ErrorResponse;
import com.logistics.scm.oms.inventory.domain.product.dto.request.ProductCreateRequest;
import com.logistics.scm.oms.inventory.domain.product.dto.request.ProductUpdateRequest;
import com.logistics.scm.oms.inventory.domain.product.dto.response.ProductResponse;
import com.logistics.scm.oms.inventory.domain.product.service.ProductService;
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
 * 상품 API Resource
 *
 * 상품 등록, 조회, 수정, 비활성화 API 제공
 *
 * @author c.h.jo
 * @since 2026-02-06
 */
@Tag(name = "상품", description = "상품 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductResource {

    private final ProductService productService;

    /**
     * 상품 등록
     * POST /api/v1/products
     */
    @Operation(
        summary = "상품 등록",
        description = "업체가 배송할 상품 정보를 시스템에 등록합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "등록 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "상품 코드 중복",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Parameter(description = "상품 등록 요청", required = true)
            @Valid @RequestBody ProductCreateRequest request) {
        log.info("상품 등록 요청: productCode={}, productName={}",
                request.getProductCode(), request.getProductName());

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "상품이 성공적으로 등록되었습니다."));
    }

    /**
     * 상품 조회 (ID)
     * GET /api/v1/products/{id}
     */
    @Operation(
        summary = "상품 조회",
        description = "ID로 상품 정보를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "상품을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @Parameter(description = "상품 ID (UUID)", required = true)
            @PathVariable UUID id) {
        log.info("상품 조회: id={}", id);

        ProductResponse response = productService.getProductById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 상품 조회 (상품 코드)
     * GET /api/v1/products/code/{productCode}
     */
    @Operation(
        summary = "상품 코드로 조회",
        description = "상품 코드로 상품 정보를 조회합니다."
    )
    @GetMapping("/code/{productCode}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByCode(
            @Parameter(description = "상품 코드", required = true)
            @PathVariable String productCode) {
        log.info("상품 조회(코드): productCode={}", productCode);

        ProductResponse response = productService.getProductByCode(productCode);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 업체별 상품 목록 조회
     * GET /api/v1/products/company/{companyId}
     */
    @Operation(
        summary = "업체별 상품 조회",
        description = "업체 ID로 해당 업체의 활성 상품 목록을 조회합니다."
    )
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCompany(
            @Parameter(description = "업체 ID (UUID)", required = true)
            @PathVariable UUID companyId) {
        log.info("업체별 상품 조회: companyId={}", companyId);

        List<ProductResponse> responses = productService.getProductsByCompany(companyId);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 상품 검색
     * GET /api/v1/products/search?keyword={}
     */
    @Operation(
        summary = "상품 검색",
        description = "상품명 키워드로 상품을 검색합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword) {
        log.info("상품 검색: keyword={}", keyword);

        List<ProductResponse> responses = productService.searchProducts(keyword);

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 상품 수정
     * PUT /api/v1/products/{id}
     */
    @Operation(
        summary = "상품 수정",
        description = "상품 정보를 수정합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "상품을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "상품 ID (UUID)", required = true)
            @PathVariable UUID id,
            @Parameter(description = "상품 수정 요청", required = true)
            @Valid @RequestBody ProductUpdateRequest request) {
        log.info("상품 수정: id={}", id);

        ProductResponse response = productService.updateProduct(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "상품이 성공적으로 수정되었습니다.")
        );
    }

    /**
     * 상품 비활성화
     * DELETE /api/v1/products/{id}
     */
    @Operation(
        summary = "상품 비활성화",
        description = "상품을 비활성화 처리합니다. (물리 삭제 아님)"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> deactivateProduct(
            @Parameter(description = "상품 ID (UUID)", required = true)
            @PathVariable UUID id) {
        log.info("상품 비활성화: id={}", id);

        ProductResponse response = productService.deactivateProduct(id);

        return ResponseEntity.ok(
                ApiResponse.success(response, "상품이 비활성화되었습니다.")
        );
    }
}
