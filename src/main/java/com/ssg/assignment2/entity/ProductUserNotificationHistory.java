package com.ssg.assignment2.entity;

import com.ssg.assignment2.entity.status.NotificationSendStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_punh_noti_hisotry_id_user_id", columnList = "notification_history_id, userId")
})
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
