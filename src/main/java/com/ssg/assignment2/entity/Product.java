package com.ssg.assignment2.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_product_stock_iteration", columnList = "stock, restockIteration")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int stock;
    private int restockIteration; //상품 재입고 회차

    public Product(String name, int stock, int restockIteration) {
        this.name = name;
        this.stock = stock;
        this.restockIteration = restockIteration;
    }

    public void decreaseStock() {
        this.stock = Math.max(0, this.stock - 1);
    }

    public void incrementRestockIteration() {
        this.restockIteration++;
    }

    public void reStock(int amount) {
        this.stock += amount;
    }
}
