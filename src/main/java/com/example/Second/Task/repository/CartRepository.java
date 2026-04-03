package com.example.Second.Task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Second.Task.entities.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
}