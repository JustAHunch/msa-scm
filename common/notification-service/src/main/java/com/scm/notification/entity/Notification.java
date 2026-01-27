package com.scm.notification.entity;

import com.scm.notification.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification Entity - 알림
 * 
 * @author c.h.jo
 * @since 2025-01-27
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "NOTIFICATION_TB", indexes = {
    @Index(name = "idx_recipient_id", columnList = "recipient_id"),
    @Index(name = "idx_recipient_type", columnList = "recipient_type"),
    @Index(name = "idx_notification_type", columnList = "notification_type"),
    @Index(name = "idx_channel", columnList = "channel"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_sent_at", columnList = "sent_at")
})
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_id", columnDefinition = "uuid")
    private UUID notificationId;

    @Column(name = "recipient_id", nullable = false, columnDefinition = "uuid")
    private UUID recipientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false, length = 20)
    private RecipientType recipientType;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 20)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private Channel channel;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    public enum RecipientType {
        CUSTOMER,  // 고객
        DRIVER,    // 배송기사
        WORKER     // 창고 작업자
    }

    public enum NotificationType {
        ORDER,      // 주문 관련
        DELIVERY,   // 배송 관련
        WAREHOUSE   // 창고 관련
    }

    public enum Channel {
        EMAIL,      // 이메일
        SMS,        // SMS
        PUSH,       // 푸시 알림
        WEBSOCKET   // 웹소켓
    }

    public enum NotificationStatus {
        PENDING,  // 대기 중
        SENT,     // 발송 완료
        FAILED    // 발송 실패
    }

    // Business Methods
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
    }

    public void markAsRead() {
        this.readAt = LocalDateTime.now();
    }

}
