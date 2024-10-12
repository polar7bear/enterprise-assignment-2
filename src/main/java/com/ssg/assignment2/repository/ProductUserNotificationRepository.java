package com.ssg.assignment2.repository;

import com.ssg.assignment2.entity.ProductUserNotification;
import com.ssg.assignment2.entity.status.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotification, Long> {
    List<ProductUserNotification> findByProductIdAndNotificationStatus(Long productId, NotificationStatus notificationStatus);
}
