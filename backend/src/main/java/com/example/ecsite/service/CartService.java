package com.example.ecsite.service;

import com.example.ecsite.entity.Cart;
import com.example.ecsite.entity.CartItem;
import com.example.ecsite.entity.Product;
import com.example.ecsite.repository.CartItemRepository;
import com.example.ecsite.repository.CartRepository;
import com.example.ecsite.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void clearCart(Long cartId) {
        cartRepository.findById(cartId).ifPresent(cart -> {
            if (cart.getCartItems() != null) {
                cart.getCartItems().clear();
                cartRepository.save(cart);
            }
        });
    }

    public java.util.List<CartItem> getCartItems(Long cartId) {
        return cartRepository.findById(cartId)
                .map(Cart::getCartItems)
                .orElse(java.util.Collections.emptyList());
    }

    @Transactional
    public Cart addToCart(Long cartId, Long productId, int quantity) {
        // カート取得または新規作成
        Cart cart = cartRepository.findById(cartId).orElseGet(() -> {
            Cart newCart = new Cart();
            return cartRepository.save(newCart);
        });

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found");
        }
        Product product = productOpt.get();

        // 既存CartItemがあれば数量加算、なければ新規
        Optional<CartItem> cartItemOpt = cart.getCartItems() == null ? Optional.empty() :
            cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        CartItem cartItem;
        if (cartItemOpt.isPresent()) {
            cartItem = cartItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
        cartRepository.save(cart);
        return cart;
    }
}
