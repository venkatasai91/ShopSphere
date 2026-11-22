
package com.shopsphere.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.CouponRequestDTO;
import com.shopsphere.dto.CouponResponseDTO;
import com.shopsphere.dto.CouponValidationRequestDTO;
import com.shopsphere.dto.CouponValidationResponseDTO;
import com.shopsphere.entity.Coupon;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CouponRepository;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    private CouponResponseDTO mapToResponse(Coupon coupon) {
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinimumOrderAmount(),
                coupon.getStartDate(),
                coupon.getExpiryDate(),
                coupon.getUsageLimit(),
                coupon.getUsedCount(),
                coupon.getActive()
        );
    }

    @Transactional
    public CouponResponseDTO createCoupon(CouponRequestDTO request) {

        // Check duplicate coupon code
        if (couponRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists");
        }

        // Validate percentage discount
        if (request.getDiscountType().equalsIgnoreCase("PERCENTAGE")
                && request.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(
                    "Percentage discount cannot exceed 100");
        }

        // Validate date range
        if (!request.getExpiryDate().isAfter(request.getStartDate())) {
            throw new IllegalArgumentException(
                    "Expiry date must be after start date");
        }

        Coupon coupon = new Coupon();

        coupon.setCode(request.getCode().trim().toUpperCase());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType().toUpperCase());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        coupon.setStartDate(request.getStartDate());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setUsedCount(0);
        coupon.setActive(request.getActive() != null
                ? request.getActive() : true);

        Coupon savedCoupon = couponRepository.save(coupon);

        return mapToResponse(savedCoupon);
    }

    @Transactional(readOnly = true)
    public List<CouponResponseDTO> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CouponResponseDTO getCouponById(Long id) {

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coupon not found with ID: " + id));

        return mapToResponse(coupon);
    }
    
    
    @Transactional
    public CouponResponseDTO updateCoupon(Long id, CouponRequestDTO request) {

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coupon not found with ID: " + id));

        // Check duplicate code, excluding the current coupon
        couponRepository.findByCodeIgnoreCase(request.getCode())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Coupon code already exists");
                    }
                });

        if (request.getDiscountType().equalsIgnoreCase("PERCENTAGE")
                && request.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(
                    "Percentage discount cannot exceed 100");
        }

        if (!request.getExpiryDate().isAfter(request.getStartDate())) {
            throw new IllegalArgumentException(
                    "Expiry date must be after start date");
        }

        coupon.setCode(request.getCode().trim().toUpperCase());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType().toUpperCase());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        coupon.setStartDate(request.getStartDate());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setActive(request.getActive());

        return mapToResponse(couponRepository.save(coupon));
    }
    @Transactional
    public void deleteCoupon(Long id) {

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coupon not found with ID: " + id));

        couponRepository.delete(coupon);
    }
    

	@Transactional(readOnly = true)
	public CouponValidationResponseDTO validateCoupon(
	        CouponValidationRequestDTO request) {
	
	    
	    Coupon coupon = couponRepository
	            .findByCodeIgnoreCase(request.getCode().trim())
	            .orElseThrow(() -> new ResourceNotFoundException(
	                    "Invalid coupon code"));
	
	    
	    if (!Boolean.TRUE.equals(coupon.getActive())) {
	        throw new IllegalArgumentException("Coupon is inactive");
	    }
	
	    
	    LocalDateTime now = LocalDateTime.now();
	
	    if (now.isBefore(coupon.getStartDate())
	            || now.isAfter(coupon.getExpiryDate())) {
	        throw new IllegalArgumentException(
	                "Coupon is not valid at this time");
	    }
	
	    
	    if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
	        throw new IllegalArgumentException(
	                "Coupon usage limit exceeded");
	    }
	
	    
	    if (request.getOrderAmount().compareTo(
	            coupon.getMinimumOrderAmount()) < 0) {
	        throw new IllegalArgumentException(
	                "Minimum order amount is "
	                + coupon.getMinimumOrderAmount());
	    }
	
	    
	    BigDecimal discountAmount;
	
	    if (coupon.getDiscountType().equals("PERCENTAGE")) {
	
	        discountAmount = request.getOrderAmount()
	                .multiply(coupon.getDiscountValue())
	                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
	
	    } else {
	
	        discountAmount = coupon.getDiscountValue();
	    }
	
	    
	    if (discountAmount.compareTo(request.getOrderAmount()) > 0) {
	        discountAmount = request.getOrderAmount();
	    }
	
	    
	    BigDecimal finalAmount = request.getOrderAmount()
	            .subtract(discountAmount);
	
	    return new CouponValidationResponseDTO(
	            coupon.getCode(),
	            "Coupon applied successfully",
	            request.getOrderAmount(),
	            discountAmount,
	            finalAmount,
	            true
	    );
	}

    
}
