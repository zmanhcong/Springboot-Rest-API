package com.example.demo2_resstapi.repository;

import com.example.demo2_resstapi.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>{
    List<Product> findByProductName(String produdctName);
}
