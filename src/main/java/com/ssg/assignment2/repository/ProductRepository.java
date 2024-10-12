package com.ssg.assignment2.repository;

import com.ssg.assignment2.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStock(int stock);
}
