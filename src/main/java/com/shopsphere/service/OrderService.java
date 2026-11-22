
package com.shopsphere.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.OrderItemResponseDTO;
import com.shopsphere.dto.OrderRequestDTO;
import com.shopsphere.dto.OrderResponseDTO;
import com.shopsphere.entity.Address;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Coupon;
import com.shopsphere.entity.Inventory;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.AddressRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.CouponRepository;
import com.shopsphere.repository.InventoryRepository;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CouponRepository couponRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            AddressRepository addressRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            CouponRepository couponRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.couponRepository = couponRepository;
    }

    private OrderItemResponseDTO mapToOrderItemResponse(OrderItem item) {

        BigDecimal subtotal = item.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new OrderItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                subtotal
        );
    }

    private OrderResponseDTO mapToResponse(Order order) {

        List<OrderItemResponseDTO> items = order.getItems()
                .stream()
                .map(this::mapToOrderItemResponse)
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getId(),
                order.getAddressId(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getCouponCode(),
                order.getOrderStatus(),
                order.getOrderDate(),
                items
        );
    }

    // Place order with inventory and coupon integration
    @Transactional
    public OrderResponseDTO placeOrder(
            Long userId,
            OrderRequestDTO request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: "
                                + request.getAddressId()));

        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Address not found for user id: " + userId);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user id: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order();

        order.setUser(user);
        order.setAddressId(address.getId());
        order.setOrderStatus("PLACED");

        // Save delivery address snapshot
        order.setDeliveryFullName(address.getFullName());
        order.setDeliveryPhone(address.getPhone());
        order.setDeliveryAddressLine(address.getAddressLine());
        order.setDeliveryCity(address.getCity());
        order.setDeliveryState(address.getState());
        order.setDeliveryCountry(address.getCountry());
        order.setDeliveryPostalCode(address.getPostalCode());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process cart items and validate inventory
        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            Inventory inventory = inventoryRepository
                    .findByProductId(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Inventory not found for product: "
                                    + product.getName()));

            int requestedQuantity = cartItem.getQuantity();

            if (requestedQuantity > inventory.getAvailableQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient inventory for product: "
                                + product.getName()
                                + ". Available quantity: "
                                + inventory.getAvailableQuantity());
            }

            // Calculate item subtotal
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(requestedQuantity));

            totalAmount = totalAmount.add(subtotal);

            // Create order item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(requestedQuantity);
            orderItem.setPrice(product.getPrice());

            order.getItems().add(orderItem);

            // Deduct inventory
            int remainingStock =
                    inventory.getAvailableQuantity() - requestedQuantity;

            inventory.setAvailableQuantity(remainingStock);
            inventoryRepository.save(inventory);

            // Synchronize product stock
            product.setStockQuantity(remainingStock);
            productRepository.save(product);
        }

        // Original order amount before discount
        BigDecimal originalAmount = totalAmount;

        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCouponCode = null;

        // Apply coupon if provided
        if (request.getCouponCode() != null
                && !request.getCouponCode().isBlank()) {

        	Coupon coupon = couponRepository
        	        .findByCodeIgnoreCaseForUpdate(request.getCouponCode().trim())
        	        .orElseThrow(() -> new ResourceNotFoundException(
        	                "Invalid coupon code"));

            // Check active status
            if (!Boolean.TRUE.equals(coupon.getActive())) {
                throw new IllegalArgumentException("Coupon is inactive");
            }

            // Check coupon validity dates
            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(coupon.getStartDate())
                    || now.isAfter(coupon.getExpiryDate())) {

                throw new IllegalArgumentException(
                        "Coupon is not valid at this time");
            }

            // Check usage limit
            if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
                throw new IllegalArgumentException(
                        "Coupon usage limit exceeded");
            }

            // Check minimum order amount
            if (originalAmount.compareTo(
                    coupon.getMinimumOrderAmount()) < 0) {

                throw new IllegalArgumentException(
                        "Minimum order amount is "
                                + coupon.getMinimumOrderAmount());
            }

            // Calculate discount
            if (coupon.getDiscountType().equalsIgnoreCase("PERCENTAGE")) {

                discountAmount = originalAmount
                        .multiply(coupon.getDiscountValue())
                        .divide(
                                new BigDecimal("100"),
                                2,
                                RoundingMode.HALF_UP
                        );

            } else {

                discountAmount = coupon.getDiscountValue();
            }

            // Discount cannot exceed order amount
            if (discountAmount.compareTo(originalAmount) > 0) {
                discountAmount = originalAmount;
            }

            // Increment coupon usage
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);

            appliedCouponCode = coupon.getCode();
        }

        // Calculate final amount after discount
        BigDecimal finalAmount = originalAmount.subtract(discountAmount);

        // Store order amounts and coupon details
        order.setDiscountAmount(discountAmount);
        order.setCouponCode(appliedCouponCode);
        order.setTotalAmount(finalAmount);

        // Save order and order items
        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order placement
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToResponse(savedOrder);
    }

    // Get all orders of a user
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrders(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId);
        }

        List<Order> orders = orderRepository
                .findByUserIdOrderByOrderDateDesc(userId);

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get order by ID
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(
            Long userId,
            Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Order not found for user id: " + userId);
        }

        return mapToResponse(order);
    }


	@Transactional
	public void cancelOrder(Long userId, Long orderId) {
	
	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new ResourceNotFoundException(
	                    "Order not found with id: " + orderId));
	
	    if (!order.getUser().getId().equals(userId)) {
	        throw new ResourceNotFoundException(
	                "Order not found for user id: " + userId);
	    }
	
	    if (!order.getOrderStatus().equals("PLACED")) {
	        throw new IllegalArgumentException(
	                "Only placed orders can be cancelled");
	    }
	
	    // Restore inventory
	    for (OrderItem item : order.getItems()) {
	
	        Product product = item.getProduct();
	
	        Inventory inventory = inventoryRepository
	                .findByProductId(product.getId())
	                .orElseThrow(() -> new ResourceNotFoundException(
	                        "Inventory not found for product: "
	                                + product.getName()));
	
	        int restoredStock =
	                inventory.getAvailableQuantity() + item.getQuantity();
	
	        inventory.setAvailableQuantity(restoredStock);
	        inventoryRepository.save(inventory);
	
	        // Synchronize product stock
	        product.setStockQuantity(restoredStock);
	        productRepository.save(product);
	    }
	
	    // Restore coupon usage if this order used a coupon
	    if (order.getCouponCode() != null
	            && !order.getCouponCode().isBlank()) {
	
	        Coupon coupon = couponRepository
	                .findByCodeIgnoreCase(order.getCouponCode())
	                .orElseThrow(() -> new ResourceNotFoundException(
	                        "Coupon not found: " + order.getCouponCode()));
	
	        if (coupon.getUsedCount() > 0) {
	            coupon.setUsedCount(coupon.getUsedCount() - 1);
	            couponRepository.save(coupon);
	        }
	    }
	
	    // Update order status
	    order.setOrderStatus("CANCELLED");
	    orderRepository.save(order);
	}

}
