package com.example.Second.Task.controller;

import com.example.Second.Task.entities.Order;
import com.example.Second.Task.entities.User;
import com.example.Second.Task.repository.OrderRepository;
import com.example.Second.Task.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // ✅ Get all orders of logged-in user
    @GetMapping
    public ResponseEntity<?> getUserOrders(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findAll()
                .stream()
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .toList();

        return ResponseEntity.ok(orders);
    }

    // ✅ Get single order
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id, Authentication authentication) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getEmail().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        return ResponseEntity.ok(order);
    }
}