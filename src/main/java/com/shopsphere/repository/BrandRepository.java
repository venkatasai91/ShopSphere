package com.shopsphere.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

	
	boolean existsByNameIgnoreCase(String name);
}
