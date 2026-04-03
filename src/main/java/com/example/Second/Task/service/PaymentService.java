package com.example.Second.Task.service;

import com.example.Second.Task.entities.Cart;
import com.example.Second.Task.entities.Order;
import com.example.Second.Task.entities.User;
import com.example.Second.Task.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final OrderRepository orderRepository;

    // =========================
    // ✅ SINGLE ORDER PAYMENT
    // =========================
    public String createPayment(Order order) throws Exception {

        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:3000/payment-success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("http://localhost:3000/payment-cancel")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("inr")
                                                        .setUnitAmount((long) (order.getTotalPrice() * 100))
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Order Payment")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        Session session = Session.create(params);

        // ✅ SAVE STRIPE SESSION ID
        order.setStripeSessionId(session.getId());
        order.setStatus("PENDING");

        orderRepository.save(order);

        return session.getUrl();
    }

    // =========================
    // ✅ CART PAYMENT
    // =========================
    public String createCartPayment(List<Cart> cartItems, User user) throws Exception {

        Stripe.apiKey = stripeSecretKey;

        long totalAmount = 0;

        for (Cart item : cartItems) {
            totalAmount += (long) (item.getProduct().getPrice() * item.getQuantity());
        }

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment-success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:3000/payment-cancel");

        for (Cart item : cartItems) {
            builder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getQuantity())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("inr")
                                            .setUnitAmount((long) (item.getProduct().getPrice() * 100))
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(item.getProduct().getName())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        Session session = Session.create(builder.build());

        // ✅ CREATE ORDER FOR CART
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice((double) totalAmount);
        order.setStatus("PENDING");
        order.setStripeSessionId(session.getId());

        orderRepository.save(order);

        return session.getUrl();
    }
}