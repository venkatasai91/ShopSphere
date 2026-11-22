
package com.shopsphere.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.AddToCartRequestDTO;
import com.shopsphere.dto.CartItemResponseDTO;
import com.shopsphere.dto.CartResponseDTO;
import com.shopsphere.dto.UpdateCartItemDTO;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Inventory;
import com.shopsphere.entity.Product;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.InventoryRepository;
import com.shopsphere.repository.ProductRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // Convert CartItem entity into Response DTO
    private CartItemResponseDTO mapToCartItemResponse(CartItem item) {

        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                subtotal
        );
    }

    // Add product to cart
    @Transactional
    public void addToCart(Long userId, AddToCartRequestDTO request) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setUpdatedAt(LocalDateTime.now());

                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        Inventory inventory = inventoryRepository
                .findByProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product id: " + product.getId()));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);

                    cart.getItems().add(newItem);

                    return newItem;
                });

        int updatedQuantity = cartItem.getQuantity() + request.getQuantity();

        if (updatedQuantity > inventory.getAvailableQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient inventory. Available quantity: "
                            + inventory.getAvailableQuantity());
        }

        cartItem.setQuantity(updatedQuantity);

        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    // Get cart
    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user id: " + userId));

        List<CartItemResponseDTO> items = cart.getItems()
                .stream()
                .map(this::mapToCartItemResponse)
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDTO(
                cart.getId(),
                cart.getUserId(),
                items,
                totalAmount);
    }

    // Update cart item quantity
    @Transactional
    public void updateCartItem(
            Long userId,
            Long itemId,
            UpdateCartItemDTO request) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user id: " + userId));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException(
                    "Cart item not found in this user's cart");
        }

        Product product = cartItem.getProduct();

        Inventory inventory = inventoryRepository
                .findByProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product id: " + product.getId()));

        if (request.getQuantity() > inventory.getAvailableQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient inventory. Available quantity: "
                            + inventory.getAvailableQuantity());
        }

        cartItem.setQuantity(request.getQuantity());

        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    // Remove cart item
    @Transactional
    public void removeCartItem(Long userId, Long itemId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user id: " + userId));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found with id: " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException(
                    "Cart item not found in this user's cart");
        }

        cartItemRepository.delete(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    // Clear cart
    @Transactional
    public void clearCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user id: " + userId));

        cart.getItems().clear();

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}
