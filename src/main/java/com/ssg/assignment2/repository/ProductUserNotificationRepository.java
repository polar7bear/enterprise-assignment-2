package com.ssg.assignment2.repository;

import com.ssg.assignment2.entity.ProductUserNotification;
import com.ssg.assignment2.entity.status.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotification, Long> {

    @Query("SELECT pun " +
            "FROM ProductUserNotification pun " +
            "WHERE pun.product.id = :productId " +
            "AND pun.notificationStatus = :status " +
            "ORDER BY pun.createdAt ASC")
    List<ProductUserNotification> findByProductIdAndNotificationStatus(Long productId, @Param("status") NotificationStatus notificationStatus);

    @Query("SELECT pun " +
            "FROM ProductUserNotification pun " +
            "WHERE pun.product.id = :productId " +
            "AND pun.notificationStatus = :status " +
            "AND pun.id > :lastId " +
            "ORDER BY pun.id ASC")
    List<ProductUserNotification> findByProductIdAndNotificationStatusAfterId(Long productId, @Param("status") NotificationStatus status, Long lastId);
}
