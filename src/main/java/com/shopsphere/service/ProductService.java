package com.shopsphere.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.ProductRequestDto;
import com.shopsphere.dto.ProductResponseDto;
import com.shopsphere.entity.Product;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.ProductRepository;


@Service
public class ProductService {

	private final ProductRepository productRepository;
	
	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	private ProductResponseDto mapToResponse(Product product) {
		return new ProductResponseDto(
				product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
		);
	}
	
	private Product mapToEntity(ProductRequestDto dto) {
		Product product=new Product();
		
		product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        
        return product;
	}
	
	
	public ProductResponseDto createProduct(ProductRequestDto dto) {
		Product product=mapToEntity(dto);
		
		Product savedProduct=productRepository.save(product);
		
		return mapToResponse(savedProduct);
	}
	
	
	public List<ProductResponseDto> getAllProducts(){
		return productRepository.findAll()
				.stream()
				.map(product -> mapToResponse(product))
				.toList();
	}
	
	
	public ProductResponseDto getProductById(Long id) {
		Product product=productRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id:" +id));
		return mapToResponse(product);
	}
	
	public ProductResponseDto updateProduct(Long id,ProductRequestDto dto) {
		Product product=productRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id: " +id));
		
		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        
        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
	}
	
	
	public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
    }
	
	
	
	
}
