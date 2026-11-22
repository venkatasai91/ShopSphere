
package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.CouponRequestDTO;
import com.shopsphere.dto.CouponResponseDTO;
import com.shopsphere.dto.CouponValidationRequestDTO;
import com.shopsphere.dto.CouponValidationResponseDTO;
import com.shopsphere.service.CouponService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    
    @PostMapping
    public ResponseEntity<CouponResponseDTO> createCoupon(
            @Valid @RequestBody CouponRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponService.createCoupon(request));
    }

  
    @GetMapping
    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {

        return ResponseEntity.ok(couponService.getAllCoupons());
    }

   
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponseDTO> getCouponById(
            @PathVariable Long id) {

        return ResponseEntity.ok(couponService.getCouponById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CouponResponseDTO> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequestDTO request) {

        return ResponseEntity.ok(
                couponService.updateCoupon(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {

        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/validate")
    public ResponseEntity<CouponValidationResponseDTO> validateCoupon(
            @Valid @RequestBody CouponValidationRequestDTO request) {

        return ResponseEntity.ok(
                couponService.validateCoupon(request));
    }
    
}
