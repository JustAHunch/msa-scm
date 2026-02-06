package com.logistics.scm.oms.inventory.domain.product.entity;

/**
 * 보관 조건
 */
public enum StorageCondition {
    ROOM_TEMP,   // 상온
    COLD,        // 냉장
    FROZEN,      // 냉동
    HAZARDOUS,   // 위험물
    FRAGILE      // 파손주의
}
