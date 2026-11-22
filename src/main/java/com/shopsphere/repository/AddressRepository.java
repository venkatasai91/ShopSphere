package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
	
	List<Address> findByUserId(Long userId);
    boolean existsByUserIdAndDefaultAddressTrue(Long userId);
    Optional<Address> findByUserIdAndDefaultAddressTrue(Long userId);
}
