
package com.shopsphere.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.InventoryRequestDTO;
import com.shopsphere.dto.InventoryResponseDTO;
import com.shopsphere.entity.Inventory;
import com.shopsphere.entity.Product;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.InventoryRepository;
import com.shopsphere.repository.ProductRepository;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository) {

        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    // Convert Inventory entity into Response DTO
    private InventoryResponseDTO mapToResponse(Inventory inventory) {

        return new InventoryResponseDTO(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getName(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.getUpdatedAt()
        );
    }

    // Create inventory for a product
    @Transactional
    public InventoryResponseDTO createInventory(
            Long productId,
            InventoryRequestDTO request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        if (inventoryRepository.existsByProductId(productId)) {
            throw new IllegalArgumentException(
                    "Inventory already exists for product id: " + productId);
        }

        Inventory inventory = new Inventory();

        inventory.setProduct(product);
        inventory.setAvailableQuantity(request.getAvailableQuantity());
        inventory.setReservedQuantity(0);

        // Keep existing Product stock synchronized
        product.setStockQuantity(request.getAvailableQuantity());
        productRepository.save(product);

        Inventory savedInventory = inventoryRepository.save(inventory);

        return mapToResponse(savedInventory);
    }

    // Get all inventory records
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get inventory using product ID
    @Transactional(readOnly = true)
    public InventoryResponseDTO getInventoryByProductId(Long productId) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product id: " + productId));

        return mapToResponse(inventory);
    }

    // Update available stock
    @Transactional
    public InventoryResponseDTO updateAvailableQuantity(
            Long productId,
            InventoryRequestDTO request) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product id: " + productId));

        if (request.getAvailableQuantity() < inventory.getReservedQuantity()) {
            throw new IllegalArgumentException(
                    "Available quantity cannot be less than reserved quantity");
        }

        inventory.setAvailableQuantity(request.getAvailableQuantity());

        Product product = inventory.getProduct();
        product.setStockQuantity(request.getAvailableQuantity());

        productRepository.save(product);

        return mapToResponse(inventoryRepository.save(inventory));
    }

    // Add stock to existing inventory
    @Transactional
    public InventoryResponseDTO addStock(
            Long productId,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity to add must be greater than zero");
        }

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product id: " + productId));

        int updatedQuantity = inventory.getAvailableQuantity() + quantity;

        inventory.setAvailableQuantity(updatedQuantity);

        Product product = inventory.getProduct();
        product.setStockQuantity(updatedQuantity);

        productRepository.save(product);

        return mapToResponse(inventoryRepository.save(inventory));
    }
}
