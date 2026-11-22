
package com.shopsphere.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.ReviewRequestDTO;
import com.shopsphere.dto.ReviewResponseDTO;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.Review;
import com.shopsphere.entity.User;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.ReviewRepository;
import com.shopsphere.repository.UserRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    
    private ReviewResponseDTO mapToResponse(Review review) {

        return new ReviewResponseDTO(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getFirstName() + " "
                        + review.getUser().getLastName(),
                review.getProduct().getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

  
    @Transactional
    public ReviewResponseDTO addReview(
            Long userId,
            Long productId,
            ReviewRequestDTO request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        if (reviewRepository.existsByUserIdAndProductId(
                userId, productId)) {

            throw new IllegalArgumentException(
                    "User has already reviewed this product");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getProductReviews(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + productId);
        }

        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getUserReviews(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId);
        }

        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ReviewResponseDTO updateReview(
            Long userId,
            Long reviewId,
            ReviewRequestDTO request) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Review not found for user id: " + userId);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);

        return mapToResponse(updatedReview);
    }

    
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Review not found for user id: " + userId);
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + productId);
        }

        List<Review> reviews =
                reviewRepository.findByProductId(productId);

        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}
