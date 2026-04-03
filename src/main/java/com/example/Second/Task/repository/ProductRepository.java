package com.example.Second.Task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Second.Task.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}