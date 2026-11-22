package com.shopsphere.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.WishlistResponseDTO;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.entity.Wishlist;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.repository.WishlistRepository;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public WishlistResponseDTO addToWishlist(Long userId, Long productId) {

      
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with id: " + userId
                    )
                );

        
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Product not found with id: " + productId
                    )
                );

       
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException(
                    "Product is already in the wishlist"
            );
        }


        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return mapToResponse(savedWishlist);
    }

    private WishlistResponseDTO mapToResponse(Wishlist wishlist) {

        Product product = wishlist.getProduct();

        return new WishlistResponseDTO(
                wishlist.getId(),
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                wishlist.getAddedAt()
        );
    }
    
    @Transactional(readOnly = true)
    public List<WishlistResponseDTO> getUserWishlist(Long userId) {

       
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        
        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(userId);

        
        return wishlistItems.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {

        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

      
        Wishlist wishlist = wishlistRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Product not found in wishlist"
                    )
                );

        
        wishlistRepository.delete(wishlist);
    }
    
}