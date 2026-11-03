package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.BrandRequestDTO;
import com.shopsphere.dto.BrandResponseDTO;
import com.shopsphere.service.BrandService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/brands")
public class BrandController {
	
	private final BrandService brandService;
	
	public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }
	
	@PostMapping
    public ResponseEntity<BrandResponseDTO> createBrand(
            @Valid @RequestBody BrandRequestDTO request) {

        BrandResponseDTO response = brandService.createBrand(request);

        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
	
	@GetMapping
	public ResponseEntity<List<BrandResponseDTO>> getAllBrands(){
		return ResponseEntity.ok(brandService.getAllBrands());
	}
	
	@GetMapping("/{id}")
    public ResponseEntity<BrandResponseDTO> getBrandById(
            @PathVariable Long id) {

        return ResponseEntity.ok(brandService.getBrandById(id));
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<BrandResponseDTO> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequestDTO request) {

        return ResponseEntity.ok(brandService.updateBrand(id, request));
    }
	
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {

        brandService.deleteBrand(id);
        
        return ResponseEntity.noContent().build();
	}
}
