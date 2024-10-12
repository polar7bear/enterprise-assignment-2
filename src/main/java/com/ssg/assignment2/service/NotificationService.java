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

        int sentCount = 0;
        for (ProductUserNotification notification : notifications) {
            if (sentCount >= 500) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (emptyStock(product, history)) return;

            try {
                sendNotificationUser(notification.getUserId());
                ProductUserNotificationHistory notificationHistory = new ProductUserNotificationHistory(
                        history, notification.getUserId(), NotificationSendStatus.SENT);
                productUserNotificationHistoryRepository.save(notificationHistory);

                notification.markAsSent();
                productUserNotificationRepository.save(notification);

                product.decreaseStock();
                productRepository.save(product);
                sentCount++;
            } catch (Exception e) {
                //해당 회원 알림발송 예외 발생 시
                ProductUserNotificationHistory notificationHistory = new ProductUserNotificationHistory(
                        history, notification.getId(), NotificationSendStatus.ERROR);
                productUserNotificationHistoryRepository.save(notificationHistory);
                history.markAsCanceledByError();
                productNotificationHistoryRepository.save(history);
                throw new RuntimeException("알림 전송 중 오류가 발생하였습니다.");
            }
        }
        history.markAsCompleted();
        productNotificationHistoryRepository.save(history);
    }

    private boolean emptyStock(Product product, ProductNotificationHistory history) {
        if (product.getStock() <= 0) {
            history.markAsCanceledBySoldOut();
            productNotificationHistoryRepository.save(history);
            return true;
        }
        return false;
    }

    private void sendNotificationUser(Long userId) {
        System.out.println("유저 " + userId + "에게 상품 재입고 알림을 전송하였습니다.");
    }
}
