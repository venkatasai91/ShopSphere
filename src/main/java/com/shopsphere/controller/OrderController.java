package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.OrderRequestDTO;
import com.shopsphere.dto.OrderResponseDTO;
import com.shopsphere.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/{userId}/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

   
    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @PathVariable Long userId,
            @Valid @RequestBody OrderRequestDTO request) {

        OrderResponseDTO response =
                orderService.placeOrder(userId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                orderService.getUserOrders(userId));
    }

    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable Long userId,
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.getOrderById(userId, orderId));
    }

    
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long userId,
            @PathVariable Long orderId) {

        orderService.cancelOrder(userId, orderId);

        return ResponseEntity.noContent().build();
    }
}
