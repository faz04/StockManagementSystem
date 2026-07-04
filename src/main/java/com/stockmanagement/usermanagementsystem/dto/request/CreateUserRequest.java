package com.stockmanagement.usermanagementsystem.dto.request;


import com.stockmanagement.usermanagementsystem.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {
	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	private String password;

	@NotNull(message = "Role is required")
	private UserRole role;

	// Default constructor
	public CreateUserRequest() {}

	// Parameterized constructor
	public CreateUserRequest(String username, String password, UserRole role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	// Getters and Setters
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public UserRole getRole() { return role; }
	public void setRole(UserRole role) { this.role = role; }
}