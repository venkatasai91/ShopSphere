
package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.ReviewRequestDTO;
import com.shopsphere.dto.ReviewResponseDTO;
import com.shopsphere.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    
    @PostMapping("/users/{userId}/reviews/{productId}")
    public ResponseEntity<ReviewResponseDTO> addReview(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.addReview(userId, productId, request));
    }

   
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getProductReviews(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.getProductReviews(productId));
    }

    
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getUserReviews(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                reviewService.getUserReviews(userId));
    }

    
    @PutMapping("/users/{userId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long userId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequestDTO request) {

        return ResponseEntity.ok(
                reviewService.updateReview(userId, reviewId, request));
    }

   
    @DeleteMapping("/users/{userId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long userId,
            @PathVariable Long reviewId) {

        reviewService.deleteReview(userId, reviewId);

        return ResponseEntity.noContent().build();
    }

 
    @GetMapping("/products/{productId}/rating")
    public ResponseEntity<Double> getAverageRating(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.getAverageRating(productId));
    }
}
