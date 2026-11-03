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

import com.shopsphere.dto.CategoryRequestDTO;
import com.shopsphere.dto.CategoryResponseDTO;
import com.shopsphere.dto.ProductResponseDto;
import com.shopsphere.service.CategoryService;
import com.shopsphere.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;
	private final ProductService productService;

	public CategoryController(CategoryService categoryService,
            ProductService productService) {
		this.categoryService = categoryService;
		this.productService = productService;
	}
	
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
	public CategoryResponseDTO createcategory(@Valid @RequestBody CategoryRequestDTO request) {
		return categoryService.createCategory(request);
	}
	
    @GetMapping
	public List<CategoryResponseDTO> getAllCategories(){
		return categoryService.getAllCategories();
	}
	
    @GetMapping("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable Long id) {
    	return categoryService.getCategoryById(id);
    }
    
    
    @PutMapping("/{id}")
    public CategoryResponseDTO updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO request) {
        return categoryService.updateCategory(id, request);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
    
    @GetMapping("/{id}/products")
    public List<ProductResponseDto> getProductsByCategory(
            @PathVariable Long id) {

        return productService.getProductsByCategory(id);
    }
}
