package com.stockmanagement.usermanagementsystem.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
	@NotBlank(message = "New password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	private String newPassword;

	// Default constructor
	public ResetPasswordRequest() {}

	// Parameterized constructor
	public ResetPasswordRequest(String newPassword) {
		this.newPassword = newPassword;
	}

	// Getters and Setters
	public String getNewPassword() { return newPassword; }
	public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}