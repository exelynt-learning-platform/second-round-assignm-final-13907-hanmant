package com.example.Second.Task.controller;

import com.example.Second.Task.entities.Cart;
import com.example.Second.Task.entities.Order;
import com.example.Second.Task.entities.User;
import com.example.Second.Task.repository.OrderRepository;
import com.example.Second.Task.repository.UserRepository;
import com.example.Second.Task.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;

    // =========================
    // ✅ SINGLE ORDER PAYMENT
    // =========================
    @PostMapping("/{orderId}")
    public ResponseEntity<?> pay(@PathVariable Long orderId, Authentication authentication) throws Exception {

        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to pay this order");
        }

        if ("PAID".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Order already paid");
        }

        String paymentLink = paymentService.createPayment(order.getTotalPrice());

        return ResponseEntity.ok(paymentLink);
    }

    // =========================
    // ✅ CONFIRM PAYMENT
    // =========================
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestParam String sessionId) {

        Order order = orderRepo.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("PAID");
        orderRepo.save(order);

        return ResponseEntity.ok("Order updated successfully");
    }

    // =========================
    // ✅ CART PAYMENT
    // =========================
    @PostMapping("/cart")
    public ResponseEntity<?> payCart(Authentication authentication) throws Exception {

        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Cart> cartItems = user.getCartItems();

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        String paymentLink = paymentService.createCartPayment(cartItems);

        return ResponseEntity.ok(paymentLink);
    }
}