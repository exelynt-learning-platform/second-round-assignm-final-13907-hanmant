package com.example.Second.Task.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data  
@NoArgsConstructor  
@AllArgsConstructor 
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private int stock;
    private String imageUrl;
}