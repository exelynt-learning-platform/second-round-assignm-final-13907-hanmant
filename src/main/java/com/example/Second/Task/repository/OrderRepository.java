package com.example.Second.Task.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Second.Task.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByStripeSessionId(String stripeSessionId);
}