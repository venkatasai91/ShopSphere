package com.shopsphere.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.ChangePasswordDTO;
import com.shopsphere.dto.UserRegistrationDTO;
import com.shopsphere.dto.UserResponseDTO;
import com.shopsphere.dto.UserUpdateDTO;
import com.shopsphere.entity.User;
import com.shopsphere.exception.ResourceNotFoundException;
import com.shopsphere.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public UserResponseDTO registerUser(UserRegistrationDTO request) {
		
		if(userRepository.existsByEmailIgnoreCase(request.getEmail())) {
			throw new IllegalArgumentException("Email is already registered");
		}
		
		User user=new User();
		
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User savedUser=userRepository.save(user);
        
        return new UserResponseDTO(
        		savedUser.getId(),
        		savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getCreatedAt()
        );
        
	}
	
	public UserResponseDTO getUserById(Long userId) {
		User user=userRepository.findById(userId)
				.orElseThrow(()->new ResourceNotFoundException("User not found with id:"+userId));
		
		return new UserResponseDTO(
				user.getId(),
				user.getFirstName(),
	            user.getLastName(),
	            user.getEmail(),
	            user.getPhone(),
	            user.getCreatedAt()
		);
	}
	
	public UserResponseDTO updateUser(Long userId, UserUpdateDTO request) {

	    User user = userRepository.findById(userId)
	            .orElseThrow(() ->
	                new ResourceNotFoundException(
	                    "User not found with id: " + userId
	                )
	            );

	    User existingUser = userRepository
	            .findByEmailIgnoreCase(request.getEmail())
	            .orElse(null);

	    if (existingUser != null && !existingUser.getId().equals(userId)) {
	        throw new IllegalArgumentException(
	                "Email is already registered by another user"
	        );
	    }

	    user.setFirstName(request.getFirstName());
	    user.setLastName(request.getLastName());
	    user.setEmail(request.getEmail());
	    user.setPhone(request.getPhone());

	    User updatedUser = userRepository.save(user);

	    return new UserResponseDTO(
	            updatedUser.getId(),
	            updatedUser.getFirstName(),
	            updatedUser.getLastName(),
	            updatedUser.getEmail(),
	            updatedUser.getPhone(),
	            updatedUser.getCreatedAt()
	    );
	}
	
	public void changePassword(Long userId,ChangePasswordDTO request) {
		User user=userRepository.findById(userId)
				.orElseThrow(()-> new ResourceNotFoundException("User not found with id:" + userId));
		
		if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new IllegalArgumentException("Current password is incorrect");
		}
		
		if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
			throw new IllegalArgumentException("New password must be different from the current password");
		}
		
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		
		userRepository.save(user);
		
	}

	public void deleteUser(Long userId) {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() ->
	                new ResourceNotFoundException(
	                    "User not found with id: " + userId
	                )
	            );

	    userRepository.delete(user);
	}
	
}
