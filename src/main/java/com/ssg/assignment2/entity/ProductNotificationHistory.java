package com.ssg.assignment2.entity;

import com.ssg.assignment2.entity.status.NotificationHistoryStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductNotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int iteration;

    @Enumerated(EnumType.STRING)
    private NotificationHistoryStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long lastSuccessUserId;

    public ProductNotificationHistory(Product product, int iteration, NotificationHistoryStatus status) {
        this.product = product;
        this.iteration = iteration;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsCompleted() {
        this.status = NotificationHistoryStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCanceledBySoldOut() {
        this.status = NotificationHistoryStatus.CANCELED_BY_SOLD_OUT;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCanceledByError() {
        this.status = NotificationHistoryStatus.CANCELED_BY_ERROR;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordLastSuccessId(Long lastSuccess) {
        this.lastSuccessUserId = lastSuccess;
    }

}
