package com.shopsphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.AddToCartRequestDTO;
import com.shopsphere.dto.CartResponseDTO;
import com.shopsphere.dto.UpdateCartItemDTO;
import com.shopsphere.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequestDTO request) {

        cartService.addToCart(userId, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(
            @PathVariable Long userId) {

        return ResponseEntity.ok(cartService.getCart(userId));
    }

    
    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Void> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemDTO request) {

        cartService.updateCartItem(userId, itemId, request);

        return ResponseEntity.ok().build();
    }

   
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {

        cartService.removeCartItem(userId, itemId);

        return ResponseEntity.noContent().build();
    }

  
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(
            @PathVariable Long userId) {

        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
}