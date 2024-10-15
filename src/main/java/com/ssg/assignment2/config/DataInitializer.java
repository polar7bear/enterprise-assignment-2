package com.ssg.assignment2.config;

import com.ssg.assignment2.entity.Product;
import com.ssg.assignment2.entity.ProductUserNotification;
import com.ssg.assignment2.entity.status.NotificationStatus;
import com.ssg.assignment2.repository.ProductRepository;
import com.ssg.assignment2.repository.ProductUserNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductUserNotificationRepository productUserNotificationRepository;

    @Override
    public void run(String... args) throws Exception {
        Product product = new Product("상품1", 100, 0);
        Product product2 = new Product("상품2", 1200, 0);
        Product product3 = new Product("상품3", 0, 0);
        productRepository.save(product);
        productRepository.save(product2);
        productRepository.save(product3);


        for (long userId = 1; userId <= product.getStock(); userId++) {
            ProductUserNotification notification = new ProductUserNotification(
                    product, userId, NotificationStatus.PENDING, LocalDateTime.now());
            productUserNotificationRepository.save(notification);
        }

        for (long userId = 1; userId <= product2.getStock(); userId++) {
            ProductUserNotification notification = new ProductUserNotification(
                    product2, userId, NotificationStatus.PENDING, LocalDateTime.now());
            productUserNotificationRepository.save(notification);
        }

        for (long userId = 1; userId <= 3; userId++) {
            ProductUserNotification notification = new ProductUserNotification(
                    product3, userId, NotificationStatus.PENDING, LocalDateTime.now());
            productUserNotificationRepository.save(notification);
        }
    }
}
