package com.logistics.scm.oms.inventory.domain.product.service;

import com.logistics.scm.oms.inventory.domain.product.dto.request.ProductCreateRequest;
import com.logistics.scm.oms.inventory.domain.product.dto.request.ProductUpdateRequest;
import com.logistics.scm.oms.inventory.domain.product.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

/**
 * 상품 서비스 인터페이스
 */
public interface ProductService {

    /**
     * 상품 등록
     *
     * @param request 상품 등록 요청
     * @return 등록된 상품 응답
     */
    ProductResponse createProduct(ProductCreateRequest request);

    /**
     * 상품 조회 (ID)
     *
     * @param productId 상품 ID
     * @return 상품 응답
     */
    ProductResponse getProductById(UUID productId);

    /**
     * 상품 조회 (상품 코드)
     *
     * @param productCode 상품 코드
     * @return 상품 응답
     */
    ProductResponse getProductByCode(String productCode);

    /**
     * 업체별 상품 목록 조회
     *
     * @param companyId 업체 ID
     * @return 상품 목록
     */
    List<ProductResponse> getProductsByCompany(UUID companyId);

    /**
     * 상품명 검색
     *
     * @param keyword 검색 키워드
     * @return 상품 목록
     */
    List<ProductResponse> searchProducts(String keyword);

    /**
     * 상품 정보 수정
     *
     * @param productId 상품 ID
     * @param request 수정 요청
     * @return 수정된 상품 응답
     */
    ProductResponse updateProduct(UUID productId, ProductUpdateRequest request);

    /**
     * 상품 비활성화
     *
     * @param productId 상품 ID
     * @return 비활성화된 상품 응답
     */
    ProductResponse deactivateProduct(UUID productId);
}
