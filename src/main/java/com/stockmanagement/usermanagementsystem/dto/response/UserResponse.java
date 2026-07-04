package com.stockmanagement.usermanagementsystem.dto.response;


import com.stockmanagement.usermanagementsystem.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UserResponse {
	private Long id;
	private String username;
	private UserRole role;
	private String roleDisplayName;
	private boolean active;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	private String createdBy;
	private String updatedBy;

	// Default constructor
	public UserResponse() {}

	// Parameterized constructor
	public UserResponse(Long id, String username, UserRole role, boolean active,
						LocalDateTime createdAt, LocalDateTime updatedAt,
						String createdBy, String updatedBy) {
		this.id = id;
		this.username = username;
		this.role = role;
		this.roleDisplayName = role != null ? role.getDisplayName() : null;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}

	// Getters and Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public UserRole getRole() { return role; }
	public void setRole(UserRole role) {
		this.role = role;
		this.roleDisplayName = role != null ? role.getDisplayName() : null;
	}

	public String getRoleDisplayName() { return roleDisplayName; }
	public void setRoleDisplayName(String roleDisplayName) { this.roleDisplayName = roleDisplayName; }

	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

	public String getCreatedBy() { return createdBy; }
	public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

	public String getUpdatedBy() { return updatedBy; }
	public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}