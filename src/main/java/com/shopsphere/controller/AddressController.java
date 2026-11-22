package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.AddressRequestDTO;
import com.shopsphere.dto.AddressResponseDTO;
import com.shopsphere.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

   
    @PostMapping
    public ResponseEntity<AddressResponseDTO> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequestDTO request) {

        AddressResponseDTO response =
                addressService.addAddress(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                addressService.getUserAddresses(userId)
        );
    }

   
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> getAddressById(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        return ResponseEntity.ok(
                addressService.getAddressById(userId, addressId)
        );
    }

   
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDTO request) {

        return ResponseEntity.ok(
                addressService.updateAddress(userId, addressId, request)
        );
    }

   
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        addressService.deleteAddress(userId, addressId);

        return ResponseEntity.noContent().build();
    }

   
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDTO> setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        return ResponseEntity.ok(
                addressService.setDefaultAddress(userId, addressId)
        );
    }
}