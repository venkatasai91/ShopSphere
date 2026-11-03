package com.shopsphere.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.BrandRequestDTO;
import com.shopsphere.dto.BrandResponseDTO;
import com.shopsphere.entity.Brand;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.BrandRepository;

@Service
public class BrandService {
	
	private final BrandRepository brandRepository;
	
	
	 public BrandService(BrandRepository brandRepository) {
	        this.brandRepository = brandRepository;
	 }
	 
	 private BrandResponseDTO mapToResponse(Brand brand) {
		 return new BrandResponseDTO(
				 brand.getId(),
				 brand.getName(),
				 brand.getDescription()
				 );
	 }
	 
	 public BrandResponseDTO createBrand(BrandRequestDTO request) {
		 if(brandRepository.existsByNameIgnoreCase(request.getName())) {
			 throw new IllegalArgumentException("Brand already exists");
		 }
		 Brand brand=new Brand();
		 brand.setName(request.getName());
		 brand.setDescription(request.getDescription());
		 
		 Brand savedbrand=brandRepository.save(brand);
		 
		 return mapToResponse(savedbrand);
	 }
	 
	 public List<BrandResponseDTO> getAllBrands(){
		 return brandRepository.findAll()
				 .stream()
				 .map(brands->mapToResponse(brands))
				 .toList();
	 }
	 
	 public BrandResponseDTO getBrandById(Long id) {

	        Brand brand = brandRepository.findById(id)
	                .orElseThrow(() ->
	                    new ResourceNotFoundException("Brand not found with id: " + id));

	        return mapToResponse(brand);
	    }
	 
	 public BrandResponseDTO updateBrand(Long id,BrandRequestDTO request) {
		 Brand brand=brandRepository.findById(id)
				 .orElseThrow(()-> new ResourceNotFoundException("Brand not found with id: " + id));
		 
		 if(!brand.getName().equalsIgnoreCase(request.getName()) && brandRepository.existsByNameIgnoreCase(request.getName())) {
			 throw new IllegalArgumentException("Brand already exists");
		 }
		 
		 
		 brand.setName(request.getName());
		 brand.setDescription(request.getDescription());
		 
		 Brand savedbrand=brandRepository.save(brand);
		 
		 return mapToResponse(savedbrand);
	 }
	 
	 public void deleteBrand(Long id) {

	        Brand brand = brandRepository.findById(id)
	                .orElseThrow(() ->
	                    new ResourceNotFoundException("Brand not found with id: " + id));

	        brandRepository.delete(brand);
	    }
}
