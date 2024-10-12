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

    /*
    * 요구사항이 좀 애매한 것 같다.
    * 상품 수량이 0개일 때 상품 재입고는 어떻게 하라는 것인지(요구사항에서는 api를 작성하라든지, 스케줄러 등을 통해 알아서 구현하라는 것인지 주어져있지않음)
    * 그리고 만약 상품 재입고로 수량을 올리게되면, 트리거를 설정하여 자동적으로 알림이 가게 하라는 것인지
    * 딱히 정해진게 없으니 위의 내용대로 알아서 해야할 것 같다. */
    // TODO: 스케줄러와 트리거 로직 구현

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
