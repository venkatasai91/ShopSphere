package com.shopsphere.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByNameIgnoreCase(String name);
}
