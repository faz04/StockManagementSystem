package com.stockmanagement.usermanagementsystem.dto.response;

import com.stockmanagement.usermanagementsystem.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class LoginResponse {
	private Long userId;
	private String username;
	private UserRole role;
	private String roleDisplayName;
	private String token;
	private boolean isAdmin;
	private List<String> permissions;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime loginTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime expiryTime;

	// Default constructor
	public LoginResponse() {
		this.loginTime = LocalDateTime.now();
	}

	// Parameterized constructor
	public LoginResponse(Long userId, String username, UserRole role, String token) {
		this.userId = userId;
		this.username = username;
		this.role = role;
		this.roleDisplayName = role.getDisplayName();
		this.token = token;
		this.isAdmin = role == UserRole.ADMIN;
		this.loginTime = LocalDateTime.now();
		this.expiryTime = LocalDateTime.now().plusHours(8); // 8 hours session
		this.permissions = generatePermissions(role);
	}

	private List<String> generatePermissions(UserRole role) {
		return switch (role) {
			case ADMIN -> List.of(
					"user.create", "user.read", "user.update", "user.delete",
					"supplier.create", "supplier.read", "supplier.update", "supplier.delete",
					"staff.create", "staff.read", "staff.update", "staff.delete",
					"inventory.create", "inventory.read", "inventory.update", "inventory.delete",
					"promotion.create", "promotion.read", "promotion.update", "promotion.delete",
					"reports.read", "system.configure"
			);
			case STOCK_MANAGER -> List.of(
					"supplier.create", "supplier.read", "supplier.update", "supplier.delete",
					"inventory.create", "inventory.read", "inventory.update", "inventory.delete",
					"reports.read"
			);
			case SALES_STAFF -> List.of(
					"customer.create", "customer.read", "customer.update",
					"promotion.read", "promotion.apply",
					"inventory.read", "billing.create"
			);
			case HR_STAFF -> List.of(
					"staff.create", "staff.read", "staff.update", "staff.delete",
					"reports.read"
			);
			case MARKETING_MANAGER -> List.of(
					"promotion.create", "promotion.read", "promotion.update", "promotion.delete",
					"reports.read", "customer.read"
			);
		};
	}

	// Getters and Setters
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public UserRole getRole() { return role; }
	public void setRole(UserRole role) {
		this.role = role;
		this.roleDisplayName = role.getDisplayName();
		this.isAdmin = role == UserRole.ADMIN;
	}

	public String getRoleDisplayName() { return roleDisplayName; }
	public void setRoleDisplayName(String roleDisplayName) { this.roleDisplayName = roleDisplayName; }

	public String getToken() { return token; }
	public void setToken(String token) { this.token = token; }

	public boolean isAdmin() { return isAdmin; }
	public void setAdmin(boolean admin) { isAdmin = admin; }

	public List<String> getPermissions() { return permissions; }
	public void setPermissions(List<String> permissions) { this.permissions = permissions; }

	public LocalDateTime getLoginTime() { return loginTime; }
	public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

	public LocalDateTime getExpiryTime() { return expiryTime; }
	public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }
}