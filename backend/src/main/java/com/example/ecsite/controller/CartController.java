package com.example.ecsite.controller;

import com.example.ecsite.entity.Cart;
import com.example.ecsite.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("")
    public ResponseEntity<Cart> addToCart(@RequestBody Map<String, Object> request) {
        Long cartId = request.get("cartId") != null ? Long.valueOf(request.get("cartId").toString()) : null;
        Long productId = Long.valueOf(request.get("productId").toString());
        int quantity = Integer.parseInt(request.get("quantity").toString());
        Cart cart = cartService.addToCart(cartId, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getCartItems(@RequestParam Long cartId) {
        return ResponseEntity.ok(cartService.getCartItems(cartId));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> request) {
        Long cartId = Long.valueOf(request.get("cartId").toString());
        cartService.clearCart(cartId);
        return ResponseEntity.ok().build();
    }
}
