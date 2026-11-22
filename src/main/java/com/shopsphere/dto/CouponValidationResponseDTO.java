
package com.shopsphere.dto;

import java.math.BigDecimal;

public class CouponValidationResponseDTO {

    private String couponCode;
    private String message;
    private BigDecimal orderAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private boolean valid;

    public CouponValidationResponseDTO(
            String couponCode,
            String message,
            BigDecimal orderAmount,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            boolean valid) {

        this.couponCode = couponCode;
        this.message = message;
        this.orderAmount = orderAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.valid = valid;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public String getMessage() {
        return message;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public boolean isValid() {
        return valid;
    }
}
