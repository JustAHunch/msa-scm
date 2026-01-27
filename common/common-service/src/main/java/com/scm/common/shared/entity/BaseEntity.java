package com.scm.common.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base Entity for Common Service
 * 
 * 모든 Entity의 공통 Audit 필드를 관리하는 추상 클래스
 * 
 * 공통 필드:
 * - createdBy: 생성자 (User ID)
 * - createdAt: 생성 일시
 * - lastModifiedBy: 최종 수정자 (User ID)
 * - lastModifiedAt: 최종 수정 일시
 * 
 * @EnableJpaAuditing과 AuditorAware 구현체를 통해 자동으로 값이 설정됨
 * 
 * 향후 개선:
 * - Spring Security Context에서 현재 사용자 ID를 가져와 설정
 * - API Gateway에서 전달된 User ID 헤더를 사용
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * 생성자 (User ID)
     * 현재는 AuditorAware 구현체에서 "SYSTEM" 반환
     * 향후 실제 사용자 ID로 대체 예정
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    /**
     * 생성 일시
     * JPA Auditing에 의해 Entity가 처음 저장될 때 자동으로 설정됨
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 최종 수정자 (User ID)
     * 현재는 AuditorAware 구현체에서 "SYSTEM" 반환
     * 향후 실제 사용자 ID로 대체 예정
     */
    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    /**
     * 최종 수정 일시
     * JPA Auditing에 의해 Entity가 수정될 때마다 자동으로 갱신됨
     */
    @LastModifiedDate
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

}
