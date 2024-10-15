package com.ssg.assignment2.repository;

import com.ssg.assignment2.entity.ProductNotificationHistory;
import com.ssg.assignment2.entity.status.NotificationHistoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductNotificationHistoryRepository extends JpaRepository<ProductNotificationHistory, Long> {
    Optional<ProductNotificationHistory> findByIdAndStatusOrderByCreatedAtAsc(Long productId, NotificationHistoryStatus notificationHistoryStatus);
}
