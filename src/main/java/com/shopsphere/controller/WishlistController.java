package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.WishlistResponseDTO;
import com.shopsphere.service.WishlistService;

@RestController
@RequestMapping("/api/users/{userId}/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    
    @PostMapping("/{productId}")
    public ResponseEntity<WishlistResponseDTO> addToWishlist(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        WishlistResponseDTO response =
                wishlistService.addToWishlist(userId, productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @GetMapping
    public ResponseEntity<List<WishlistResponseDTO>> getUserWishlist(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                wishlistService.getUserWishlist(userId)
        );
    }

    
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        wishlistService.removeFromWishlist(userId, productId);

        return ResponseEntity.noContent().build();
    }
}