package com.shopsphere.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDTO {

	private Long cartId;
	private Long userId;
	private List<CartItemResponseDTO> items;
	private BigDecimal totalAmoutn;
	public CartResponseDTO(Long cartId, Long userId, List<CartItemResponseDTO> items, BigDecimal totalAmoutn) {
		this.cartId = cartId;
		this.userId = userId;
		this.items = items;
		this.totalAmoutn = totalAmoutn;
	}
	public Long getCartId() {
		return cartId;
	}
	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public List<CartItemResponseDTO> getItems() {
		return items;
	}
	public void setItems(List<CartItemResponseDTO> items) {
		this.items = items;
	}
	public BigDecimal getTotalAmoutn() {
		return totalAmoutn;
	}
	public void setTotalAmoutn(BigDecimal totalAmoutn) {
		this.totalAmoutn = totalAmoutn;
	}
	
	
}
