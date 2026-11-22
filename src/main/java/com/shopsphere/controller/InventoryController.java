
package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.InventoryRequestDTO;
import com.shopsphere.dto.InventoryResponseDTO;
import com.shopsphere.service.InventoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    
    @PostMapping("/products/{productId}")
    public ResponseEntity<InventoryResponseDTO> createInventory(
            @PathVariable @Positive Long productId,
            @Valid @RequestBody InventoryRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(productId, request));
    }

    
    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventory() {

        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<InventoryResponseDTO> getInventoryByProductId(
            @PathVariable @Positive Long productId) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByProductId(productId));
    }

    
    @PutMapping("/products/{productId}")
    public ResponseEntity<InventoryResponseDTO> updateAvailableQuantity(
            @PathVariable @Positive Long productId,
            @Valid @RequestBody InventoryRequestDTO request) {

        return ResponseEntity.ok(
                inventoryService.updateAvailableQuantity(productId, request));
    }

    
    @PatchMapping("/products/{productId}/add-stock")
    public ResponseEntity<InventoryResponseDTO> addStock(
            @PathVariable @Positive Long productId,
            @RequestParam @Positive Integer quantity) {

        return ResponseEntity.ok(
                inventoryService.addStock(productId, quantity));
    }
}
