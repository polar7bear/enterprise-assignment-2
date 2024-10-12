package com.ssg.assignment2.controller;

import com.ssg.assignment2.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductNotificationController {

    private final NotificationService notificationService;

    @PostMapping("/products/{productId}/notifications/re-stock")
    public ResponseEntity<Void> restockNotifications(@PathVariable Long productId) {
        notificationService.sendRestockNotification(productId);
        return ResponseEntity.ok().build();
    }

    /*@PostMapping("/admin/products/{productId}}/notifications/re-stock")
    public ResponseEntity<Void> sendRestockNotifications() {
        return null;
    }*/
}
