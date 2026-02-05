package com.logistics.scm.oms.order.domain.customer.entity;

import com.logistics.scm.oms.order.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Customer Entity
 * 
 * 고객 정보를 관리합니다.
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "CUSTOMER_TB", indexes = {
    @Index(name = "idx_customer_email", columnList = "email", unique = true),
    @Index(name = "idx_customer_phone", columnList = "phone_number"),
    @Index(name = "idx_customer_type", columnList = "customer_type")
})
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id", columnDefinition = "uuid")
    private UUID customerId;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 20)
    private CustomerType customerType;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    // Enum for Customer Type
    public enum CustomerType {
        INDIVIDUAL,   // 개인
        CORPORATE     // 기업
    }

    // Business Methods
    public void addAddress(Address address) {
        addresses.add(address);
        address.setCustomer(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setCustomer(null);
    }

    public void updateContactInfo(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
