package com.logistics.scm.oms.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration
 * 
 * JPA Auditing을 활성화합니다.
 * - @CreatedDate, @LastModifiedDate 자동 설정
 * - @CreatedBy, @LastModifiedBy 자동 설정 (AuditorAware 필요)
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
