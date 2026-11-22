package com.shopsphere.dto;

import java.math.BigDecimal;

public class CartItemResponseDTO {
	
	private Long cartItemId;
	private Long productId;
	private String ProductName;
	private BigDecimal price;
	private Integer quantity;
	private BigDecimal subtotal;
	public CartItemResponseDTO(Long cartItemId, Long productId, String productName, BigDecimal price, Integer quantity,
			BigDecimal subtotal) {
		this.cartItemId = cartItemId;
		this.productId = productId;
		ProductName = productName;
		this.price = price;
		this.quantity = quantity;
		this.subtotal = subtotal;
	}
	
	
	public Long getCartItemId() {
		return cartItemId;
	}
	public void setCartItemId(Long cartItemId) {
		this.cartItemId = cartItemId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return ProductName;
	}
	public void setProductName(String productName) {
		ProductName = productName;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
	
	
	
}
