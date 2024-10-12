package com.ssg.assignment2.service;

import com.ssg.assignment2.entity.Product;
import com.ssg.assignment2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRestockScheduled {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void checkAndRestock() {
        List<Product> products = productRepository.findByStock(0);
        log.info("[ProductRestockScheduled checkAndRestock] 상품이 재입고 되었습니다.");
        for (Product product : products) {
            product.reStock(10);
            productRepository.save(product);

            notificationService.sendRestockNotification(product.getId());

        }
    }

}
