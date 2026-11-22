package com.shopsphere.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {

    private Long orderId;
    private Long userId;
    private Long addressId;
    private BigDecimal totalAmount;
    private String orderStatus;
    private LocalDateTime orderDate;
    private BigDecimal discountAmount;
    private String couponCode;

    private List<OrderItemResponseDTO> items;

    public OrderResponseDTO() {
    }

    public OrderResponseDTO(
            Long orderId,
            Long userId,
            Long addressId,
            BigDecimal totalAmount,
            BigDecimal discountAmount,
            String couponCode,
            String orderStatus,
            LocalDateTime orderDate,
            List<OrderItemResponseDTO> items) {

        this.orderId = orderId;
        this.userId = userId;
        this.addressId = addressId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.couponCode = couponCode;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.items = items;
    }

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public List<OrderItemResponseDTO> getItems() {
		return items;
	}

	public void setItems(List<OrderItemResponseDTO> items) {
		this.items = items;
	}
	
	public BigDecimal getDiscountAmount() {
	    return discountAmount;
	}

	public String getCouponCode() {
	    return couponCode;
	}

    
}