package com.shopsphere.controller;



import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.ProductRequestDto;
import com.shopsphere.dto.ProductResponseDto;
import com.shopsphere.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/products")
@Validated
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
	
	@GetMapping
	public Page<ProductResponseDto> getAllProducts(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue = "5") int size,@RequestParam(defaultValue = "id") String sortBy,@RequestParam(defaultValue = "asc") String direction){
		return productService.getAllProducts(page,size,sortBy,direction);
	}
	@GetMapping("/search")
	public List<ProductResponseDto> searchProducts(
	        @RequestParam @NotBlank(message="search name canaot be empty")
	        String name) {

	    return productService.searchProducts(name);
	}

	@GetMapping("/filter")
	public List<ProductResponseDto> filterProducts(
	        @RequestParam @NotNull(message="minimum price is required")
	        @DecimalMin(value="0.0",message="Minimum price cannot be negative")
	        BigDecimal minPrice,
	        @RequestParam @NotNull(message="Maximum price is required") 
	        @DecimalMin(value = "0.0", message = "Maximum price cannot be negative")
	        BigDecimal maxPrice) {
		
		if(minPrice.compareTo(maxPrice)>0) {
			throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
		}
		
	    return productService.filterProducts(minPrice, maxPrice);
	}
}
