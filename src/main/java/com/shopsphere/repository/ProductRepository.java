package com.shopsphere.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopsphere.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("select p from Product p where LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%')) ")
	List<Product> searchProductsByName(@Param("name") String name);
	List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
	List<Product> findByCategoryId(Long categoryId);
}
