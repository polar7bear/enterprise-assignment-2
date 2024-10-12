package com.ssg.assignment2.entity;

import com.ssg.assignment2.entity.status.NotificationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    private LocalDateTime createdAt;

    private Long lastSuccessUserId;

    public ProductUserNotification(Product product, Long userId, NotificationStatus notificationStatus, LocalDateTime createdAt, Long lastSuccessUserId) {
        this.product = product;
        this.userId = userId;
        this.notificationStatus = notificationStatus;
        this.createdAt = createdAt;
        this.lastSuccessUserId = lastSuccessUserId;
    }

    public void markAsSent() {
        this.notificationStatus = NotificationStatus.SENT;
    }

}
