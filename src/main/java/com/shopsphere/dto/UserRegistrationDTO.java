package com.shopsphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {
	
	@NotBlank(message="First name is required")
	@Size(max=100,message="First name cannot exceed 100 characters")
	private String firstName;
	
	@NotBlank(message="Last name is required")
	@Size(max=100,message="Last name cannot exceed 100 characters")
	private String lastName;
	
	@NotBlank(message="Email is required")
	@Email(message="Enter a valid email address")
	@Size(max=150)
	private String email;
	
	@NotBlank(message = "Password is required")
    @Size(min = 8, max = 72,message = "Password must contain 8 to 72 characters")
	private String password;
	
	@Pattern(regexp = "^[0-9]{10,15}$",message = "Phone number must contain 10 to 15 digits")
	private String phone;

	
	public UserRegistrationDTO() {
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	
}
