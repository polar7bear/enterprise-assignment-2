package com.ssg.assignment2.entity;

import com.ssg.assignment2.entity.status.NotificationSendStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUserNotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notification_history_id")
    private ProductNotificationHistory notificationHistory;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private NotificationSendStatus status;

    private LocalDateTime createdAt;

    public ProductUserNotificationHistory(ProductNotificationHistory notificationHistory, Long userId, NotificationSendStatus status) {
        this.notificationHistory = notificationHistory;
        this.userId = userId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

}
