package com.shopsphere.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.CategoryRequestDTO;
import com.shopsphere.dto.CategoryResponseDTO;
import com.shopsphere.entity.Category;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.CategoryRepository;

@Service
public class CategoryService {
	
	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	private CategoryResponseDTO mapToResponse(Category category) {
		return new CategoryResponseDTO(
				category.getId(),
                category.getName(),
                category.getDescription()
				
		);
	}
//	private CategoryRequestDTO mapToEntity(CategoryRequestDTO dto) {
//		Category category=new Category();
//		
//		category.setName(dto.getName());
//		category.setDescription(dto.getDescription());
//		
//		return category;
//	}
	
	public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
		
		if(categoryRepository.existsByNameIgnoreCase(request.getName())) {
			throw new IllegalArgumentException("category already exists");
		}
		
		Category category=new Category();
		category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        Category savedCategory = categoryRepository.save(category);
        
        return mapToResponse(savedCategory);
	}
	
	public List<CategoryResponseDTO> getAllCategories(){
		 return categoryRepository.findAll()
	                .stream()
	                .map(this::mapToResponse)
	                .toList();
	}
	public CategoryResponseDTO getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id: " + id));

        return mapToResponse(category);
    }
	
	public CategoryResponseDTO updateCategory(Long id,CategoryRequestDTO request) {
		Category category=categoryRepository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Category not found with ID: " + id));
		boolean nameExists=categoryRepository.existsByNameIgnoreCase(request.getName());
		if(nameExists && !category.getName().equalsIgnoreCase(request.getName())) {
			throw new IllegalArgumentException("Category name already exists");
		}
		
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		Category updatedCategory = categoryRepository.save(category);
		
		return mapToResponse(updatedCategory);
	}
	
	public void deleteCategory(Long id) {
		Category category=categoryRepository.findById(id)
				.orElseThrow(()->new ResourceNotFoundException("Category not found with ID: " + id));
		
		categoryRepository.delete(category);
	}
	
}
