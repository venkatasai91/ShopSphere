package com.shopsphere.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderRequestDTO {

    @NotNull(message = "Address ID is required")
    @Positive(message = "Address ID must be positive")
    private Long addressId;
    
    private String couponCode;

    public OrderRequestDTO() {
    }

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
    
	public String getCouponCode() {
	    return couponCode;
	}

	public void setCouponCode(String couponCode) {
	    this.couponCode = couponCode;
	}
    
}