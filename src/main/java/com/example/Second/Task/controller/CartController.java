package com.example.Second.Task.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.Second.Task.entities.Cart;
import com.example.Second.Task.entities.User;
import com.example.Second.Task.entities.Product;
import com.example.Second.Task.repository.CartRepository;
import com.example.Second.Task.repository.ProductRepository;
import com.example.Second.Task.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartRepository repo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    // ✅ DTO for request body
    public static class CartRequest {
        private Long productId;
        private int quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    // ✅ ADD TO CART (JWT USER)
    @PostMapping
    public Cart add(@RequestBody CartRequest request,
                    Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(request.getQuantity());

        return repo.save(cart);
    }

    // ✅ GET USER CART (JWT USER)
    @GetMapping
    public List<Cart> get(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return repo.findByUserId(user.getId());
    }

    // ✅ REMOVE ITEM FROM CART
    @DeleteMapping("/{id}")
    public String remove(@PathVariable Long id) {
        repo.deleteById(id);
        return "Removed from cart";
    }
}