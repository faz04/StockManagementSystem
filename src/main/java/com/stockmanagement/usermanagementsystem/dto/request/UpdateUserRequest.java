package com.stockmanagement.usermanagementsystem.dto.request;


import com.stockmanagement.usermanagementsystem.entity.UserRole;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
	private String username;

	private UserRole role;

	private Boolean active;

	// Default constructor
	public UpdateUserRequest() {}

	// Getters and Setters
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public UserRole getRole() { return role; }
	public void setRole(UserRole role) { this.role = role; }

	public Boolean getActive() { return active; }
	public void setActive(Boolean active) { this.active = active; }
}
