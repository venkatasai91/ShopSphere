package com.shopsphere.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.PaymentRequestDTO;
import com.shopsphere.dto.PaymentResponseDTO;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.Payment;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    private PaymentResponseDTO mapToResponse(Payment payment) {

        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getTransactionId(),
                payment.getPaymentDate()
        );
    }

    @Transactional
    public PaymentResponseDTO initiatePayment(
            Long userId, PaymentRequestDTO request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + request.getOrderId()));

        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Order not found for user id: " + userId);
        }

        if (paymentRepository.existsByOrderIdAndPaymentStatus(
                order.getId(), "SUCCESS")) {

            throw new IllegalArgumentException(
                    "Payment has already been completed for this order");
        }
        if (!order.getOrderStatus().equals("PLACED")) {
            throw new IllegalArgumentException(
                    "Payment can only be initiated for placed orders");
        }

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod().toUpperCase());
        payment.setPaymentStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        return mapToResponse(savedPayment);
    }
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getOrderPayments(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Order not found for user id: " + userId);
        }

        List<Payment> payments = paymentRepository.findByOrderId(orderId);

        return payments.stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(
            Long paymentId, String status) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + paymentId));

        if (!status.equalsIgnoreCase("SUCCESS")
                && !status.equalsIgnoreCase("FAILED")) {

            throw new IllegalArgumentException(
                    "Payment status must be SUCCESS or FAILED");
        }

        if (!payment.getPaymentStatus().equals("PENDING")) {
            throw new IllegalArgumentException(
                    "Only pending payments can be updated");
        }

        payment.setPaymentStatus(status.toUpperCase());

        if ("SUCCESS".equalsIgnoreCase(status)) {
            Order order = payment.getOrder();
            order.setOrderStatus("CONFIRMED");
            orderRepository.save(order);
        }

        Payment updatedPayment = paymentRepository.save(payment);

        return mapToResponse(updatedPayment);
    }
}