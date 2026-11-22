
package com.shopsphere.dto;

import java.time.LocalDateTime;

public class InventoryResponseDTO {

    private Long inventoryId;
    private Long productId;
    private String productName;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private LocalDateTime updatedAt;

    public InventoryResponseDTO(
            Long inventoryId,
            Long productId,
            String productName,
            Integer availableQuantity,
            Integer reservedQuantity,
            LocalDateTime updatedAt) {

        this.inventoryId = inventoryId;
        this.productId = productId;
        this.productName = productName;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.updatedAt = updatedAt;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
