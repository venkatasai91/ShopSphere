package com.shopsphere.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.AddressRequestDTO;
import com.shopsphere.dto.AddressResponseDTO;
import com.shopsphere.entity.Address;
import com.shopsphere.entity.User;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.AddressRepository;
import com.shopsphere.repository.UserRepository;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository,
                          UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AddressResponseDTO addAddress(Long userId, AddressRequestDTO request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with id: " + userId
                    )
                );

        Address address = new Address();

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        boolean hasDefaultAddress =
                addressRepository.existsByUserIdAndDefaultAddressTrue(userId);

        if (!hasDefaultAddress) {
            address.setDefaultAddress(true);
        } else {
            address.setDefaultAddress(request.isDefaultAddress());
        }

        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        return mapToResponse(savedAddress);
    }
    
    
    private AddressResponseDTO mapToResponse(Address address) {

        return new AddressResponseDTO(
                address.getId(),
                address.getFullName(),
                address.getPhone(),
                address.getAddressLine(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPostalCode(),
                address.isDefaultAddress()
        );
    }
    
    @Transactional(readOnly = true)
    public List<AddressResponseDTO> getUserAddresses(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }
        

        List<Address> addresses = addressRepository.findByUserId(userId);

        return addresses.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    
    @Transactional(readOnly = true)
    public AddressResponseDTO getAddressById(Long userId, Long addressId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Address not found with id: " + addressId
                    )
                );

        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Address not found for user id: " + userId
            );
        }

        return mapToResponse(address);
    }
    
    @Transactional
    public AddressResponseDTO updateAddress(
            Long userId, Long addressId, AddressRequestDTO request) {

  
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Address not found with id: " + addressId
                    )
                );

      
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Address not found for user id: " + userId
            );
        }

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        address.setDefaultAddress(request.isDefaultAddress());

        Address updatedAddress = addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }
    
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {

       
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

       
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Address not found with id: " + addressId
                    )
                );

   
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Address not found for user id: " + userId
            );
        }

        addressRepository.delete(address);
    }
    
    @Transactional
    public AddressResponseDTO setDefaultAddress(Long userId, Long addressId) {

        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

   
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Address not found with id: " + addressId
                    )
                );

        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Address not found for user id: " + userId
            );
        }

        addressRepository.findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(currentDefault -> {
                    currentDefault.setDefaultAddress(false);
                    addressRepository.save(currentDefault);
                });

        address.setDefaultAddress(true);

        Address updatedAddress = addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }
    
}