package com.scm.common.user.entity;

/**
 * User Role Enum
 * 
 * 시스템 사용자의 역할(Role)을 정의하는 Enum
 * 
 * 역할별 설명:
 * - ADMIN: 시스템 관리자
 *   * 전체 시스템 관리 권한
 *   * 사용자 관리, 설정 변경 등
 * 
 * - MANAGER: 관리자
 *   * 창고/배송 관리자
 *   * 주문 관리, 재고 관리, 배송 계획 수립 등
 * 
 * - OPERATOR: 현장 작업자
 *   * 창고 피커/패커, 배송기사 등
 *   * 피킹/패킹 작업, 배송 실행 등
 * 
 * - CUSTOMER: 고객
 *   * 일반 주문 고객
 *   * 주문 조회, 배송 추적 등
 * 
 * 향후 확장 가능한 역할:
 * - SUPERVISOR: 감독자 (현장 관리)
 * - ANALYST: 분석가 (데이터 분석 전용)
 */
public enum Role {
    ADMIN,      // 시스템 관리자
    MANAGER,    // 관리자
    OPERATOR,   // 현장 작업자
    CUSTOMER    // 고객
}
