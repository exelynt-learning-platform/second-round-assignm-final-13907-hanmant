package com.example.Second.Task.controller;

import com.example.Second.Task.entities.Order;
import com.example.Second.Task.repository.OrderRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final OrderRepository orderRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public String handleWebhook(@RequestBody String payload,
                                @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return "Invalid signature";
        }

        System.out.println("Event received: " + event.getType());

        if ("checkout.session.completed".equals(event.getType())) {

            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (session != null) {

                System.out.println("Session metadata: " + session.getMetadata());

                String orderId = session.getMetadata().get("orderId");

                if (orderId == null) {
                    return "orderId missing";
                }

                Order order = orderRepository.findById(Long.parseLong(orderId))
                        .orElseThrow(() -> new RuntimeException("Order not found"));

                // ✅ Mark order as PAID
                order.setStatus("PAID");
                orderRepository.save(order);

                System.out.println("Order updated to PAID");
            }
        }

        return "success";
    }
}