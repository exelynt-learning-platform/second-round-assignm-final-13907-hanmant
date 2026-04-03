package com.example.Second.Task.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String status;
    private Double totalAmount;
    private Double totalPrice;

    private String stripeSessionId;

    @ManyToOne
    private User user;
}