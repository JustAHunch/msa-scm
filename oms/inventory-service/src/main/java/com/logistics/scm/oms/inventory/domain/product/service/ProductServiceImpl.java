package com.logistics.scm.oms.inventory.domain.product.service;

import com.logistics.scm.oms.inventory.domain.product.dto.request.ProductCreateRequest;
import com.logistics.scm.oms.inventory.domain.product.dto.request.ProductUpdateRequest;
import com.logistics.scm.oms.inventory.domain.product.dto.response.ProductResponse;
import com.logistics.scm.oms.inventory.domain.product.entity.Product;
import com.logistics.scm.oms.inventory.domain.product.entity.ProductStatus;
import com.logistics.scm.oms.inventory.domain.product.exception.DuplicateProductCodeException;
import com.logistics.scm.oms.inventory.domain.product.exception.ProductNotFoundException;
import com.logistics.scm.oms.inventory.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 상품 서비스 구현체
 *
 * 상품 등록, 조회, 수정, 비활성화 비즈니스 로직
 *
 * @author c.h.jo
 * @since 2026-02-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("상품 등록 시작: productCode={}, productName={}, companyId={}",
                request.getProductCode(), request.getProductName(), request.getCompanyId());

        // 상품 코드 중복 확인
        if (productRepository.existsByProductCode(request.getProductCode())) {
            throw new DuplicateProductCodeException(request.getProductCode());
        }

        Product product = Product.builder()
                .productCode(request.getProductCode())
                .productName(request.getProductName())
                .description(request.getDescription())
                .category(request.getCategory())
                .specification(request.getSpecification())
                .unit(request.getUnit())
                .weight(request.getWeight())
                .width(request.getWidth())
                .height(request.getHeight())
                .depth(request.getDepth())
                .storageCondition(request.getStorageCondition())
                .companyId(request.getCompanyId())
                .status(ProductStatus.ACTIVE)
                .build();

        Product savedProduct = productRepository.save(product);

        log.info("상품 등록 완료: productId={}, productCode={}",
                savedProduct.getId(), savedProduct.getProductCode());

        return ProductResponse.from(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {
        log.debug("상품 조회(ID): productId={}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        return ProductResponse.from(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByCode(String productCode) {
        log.debug("상품 조회(코드): productCode={}", productCode);

        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ProductNotFoundException(productCode));

        return ProductResponse.from(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCompany(UUID companyId) {
        log.debug("업체별 상품 조회: companyId={}", companyId);

        return productRepository.findByCompanyIdAndStatus(companyId, ProductStatus.ACTIVE).stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        log.debug("상품 검색: keyword={}", keyword);

        return productRepository.findByProductNameContainingIgnoreCase(keyword).stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest request) {
        log.info("상품 수정: productId={}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.update(
                request.getProductName(),
                request.getDescription(),
                request.getCategory(),
                request.getSpecification(),
                request.getUnit(),
                request.getWeight(),
                request.getStorageCondition()
        );

        Product savedProduct = productRepository.save(product);

        log.info("상품 수정 완료: productId={}", savedProduct.getId());

        return ProductResponse.from(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse deactivateProduct(UUID productId) {
        log.info("상품 비활성화: productId={}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.deactivate();
        Product savedProduct = productRepository.save(product);

        log.info("상품 비활성화 완료: productId={}", savedProduct.getId());

        return ProductResponse.from(savedProduct);
    }
}
