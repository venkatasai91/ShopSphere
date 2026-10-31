package com.shopsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.ProductRequestDto;
import com.shopsphere.dto.ProductResponseDto;
import com.shopsphere.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	
	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductResponseDto createProduct(
            @Valid @RequestBody ProductRequestDto dto) {

        return productService.createProduct(dto);
    }
	
	@GetMapping
	public List<ProductResponseDto> getAllProducts() {

        return productService.getAllProducts();
    }
	
	@GetMapping("/{id}")
	public ProductResponseDto getProductById(@PathVariable Long id) {
		return productService.getProductById(id);
	}
	
	@PutMapping("/{id}")
	public ProductResponseDto updateProduct(@PathVariable Long id,@Valid @RequestBody ProductRequestDto dto) {
		return productService.updateProduct(id, dto);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
	}
}
