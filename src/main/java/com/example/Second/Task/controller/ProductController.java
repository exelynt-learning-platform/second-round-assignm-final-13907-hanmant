package com.example.Second.Task.controller;

import org.springframework.web.bind.annotation.*;

import com.example.Second.Task.entities.Product;
import com.example.Second.Task.repository.ProductRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository repo;  

    @PostMapping
    public Product create(@RequestBody Product p) {
        return repo.save(p);
    }

    @GetMapping
    public List<Product> getAll() {
        return repo.findAll();
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product p) {
        Product existing = repo.findById(id).orElseThrow();

        existing.setName(p.getName());
        existing.setPrice(p.getPrice());
        existing.setStock(p.getStock());

        return repo.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}