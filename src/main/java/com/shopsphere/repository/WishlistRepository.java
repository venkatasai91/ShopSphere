package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
	List<Wishlist> findByUserId(Long userId);
	Optional<Wishlist> findByUserIdAndProductId(Long userId,Long productId);
	boolean existsByUserIdAndProductId(Long userId, Long productId);
}
