package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.PaymentRequestDTO;
import com.shopsphere.dto.PaymentResponseDTO;
import com.shopsphere.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/{userId}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

   
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @PathVariable Long userId,
            @Valid @RequestBody PaymentRequestDTO request) {

        PaymentResponseDTO response =
                paymentService.initiatePayment(userId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getOrderPayments(
            @PathVariable Long userId,
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.getOrderPayments(userId, orderId));
    }

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(paymentId, status));
    }
}