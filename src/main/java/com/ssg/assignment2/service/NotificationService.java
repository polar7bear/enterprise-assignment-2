package com.ssg.assignment2.service;

import com.ssg.assignment2.entity.Product;
import com.ssg.assignment2.entity.ProductNotificationHistory;
import com.ssg.assignment2.entity.ProductUserNotification;
import com.ssg.assignment2.entity.ProductUserNotificationHistory;
import com.ssg.assignment2.entity.status.NotificationHistoryStatus;
import com.ssg.assignment2.entity.status.NotificationSendStatus;
import com.ssg.assignment2.entity.status.NotificationStatus;
import com.ssg.assignment2.repository.ProductNotificationHistoryRepository;
import com.ssg.assignment2.repository.ProductRepository;
import com.ssg.assignment2.repository.ProductUserNotificationHistoryRepository;
import com.ssg.assignment2.repository.ProductUserNotificationRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {


    private final ProductRepository productRepository;
    private final ProductNotificationHistoryRepository productNotificationHistoryRepository;
    private final ProductUserNotificationRepository productUserNotificationRepository;
    private final ProductUserNotificationHistoryRepository productUserNotificationHistoryRepository;


    @Transactional
    @RateLimiter(name = "send-notification")
    public void sendRestockNotification(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));

        product.incrementRestockIteration();
        productRepository.save(product);

        ProductNotificationHistory history = new ProductNotificationHistory(
                product,
                product.getRestockIteration(),
                NotificationHistoryStatus.IN_PROGRESS);

        productNotificationHistoryRepository.save(history);

        List<ProductUserNotification> notifications = productUserNotificationRepository
                .findByProductIdAndNotificationStatus(productId, NotificationStatus.PENDING);

        processNotification(history, notifications, product);
    }

    @Transactional
    @RateLimiter(name = "send-notification")
    public void sendManualRestockNotification(Long productId) {
        ProductNotificationHistory history = productNotificationHistoryRepository
                .findByIdAndStatusOrderByCreatedAtAsc(productId, NotificationHistoryStatus.CANCELED_BY_ERROR)
                .orElseThrow(() -> new RuntimeException("실패한 알림 기록이 없습니다."));

        List<ProductUserNotification> notifications = productUserNotificationRepository
                .findByProductIdAndNotificationStatusAfterId(productId, NotificationStatus.PENDING, history.getLastSuccessUserId());

        Product product = history.getProduct();
        processNotification(history, notifications, product);
    }

    private void processNotification(ProductNotificationHistory history, List<ProductUserNotification> notifications, Product product) {
        for (ProductUserNotification notification : notifications) {
            if (emptyStock(product, history)) return;

            try {
                sendSuccess(notification, history, product);
            } catch (Exception e) {
                cancleByError(notification, history);
            }
        }
        history.markAsCompleted();
        productNotificationHistoryRepository.save(history);
    }

    private void sendSuccess(ProductUserNotification notification, ProductNotificationHistory history, Product product) {
        sendNotificationUser(notification.getUserId());
        ProductUserNotificationHistory notificationHistory = new ProductUserNotificationHistory(
                history, notification.getUserId(), NotificationSendStatus.SENT);
        productUserNotificationHistoryRepository.save(notificationHistory);

        notification.markAsSent();
        productUserNotificationRepository.save(notification);

        productRepository.save(product);
    }

    private void cancleByError(ProductUserNotification notification, ProductNotificationHistory history) {
        ProductUserNotificationHistory notificationHistory = new ProductUserNotificationHistory(
                history, notification.getId(), NotificationSendStatus.ERROR);
        productUserNotificationHistoryRepository.save(notificationHistory);
        history.markAsCanceledByError();
        history.recordLastSuccessId(notification.getUserId());
        productNotificationHistoryRepository.save(history);
        throw new RuntimeException("알림 전송 중 오류가 발생하였습니다.");
    }

    private boolean emptyStock(Product product, ProductNotificationHistory history) {
        if (product.getStock() <= 0) {
            history.markAsCanceledBySoldOut();
            productNotificationHistoryRepository.save(history);
            return true;
        }
        return false;
    }

    public void sendNotificationUser(Long userId) {
        System.out.println("유저 " + userId + "에게 상품 재입고 알림을 전송하였습니다.");
    }
}
