package com.scm.notification.entity;

import com.scm.notification.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * NotificationTemplate Entity - 알림 템플릿
 * 
 * @author c.h.jo
 * @since 2026-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "NOTIFICATION_TEMPLATE_TB", indexes = {
    @Index(name = "idx_template_code", columnList = "template_code", unique = true),
    @Index(name = "idx_channel", columnList = "channel"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
public class NotificationTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "template_id", columnDefinition = "uuid")
    private UUID templateId;

    @Column(name = "template_code", nullable = false, unique = true, length = 50)
    private String templateCode;

    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private Notification.Channel channel;

    @Column(name = "subject_template", length = 200)
    private String subjectTemplate;

    @Column(name = "body_template", columnDefinition = "TEXT")
    private String bodyTemplate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // Business Methods
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

}
