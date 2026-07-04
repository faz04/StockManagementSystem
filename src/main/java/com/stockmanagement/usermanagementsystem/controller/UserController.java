package com.stockmanagement.usermanagementsystem.controller;


import com.stockmanagement.usermanagementsystem.dto.request.CreateUserRequest;
import com.stockmanagement.usermanagementsystem.dto.request.ResetPasswordRequest;
import com.stockmanagement.usermanagementsystem.dto.request.UpdateUserRequest;
import com.stockmanagement.usermanagementsystem.dto.response.ApiResponse;
import com.stockmanagement.usermanagementsystem.dto.response.UserResponse;
import com.stockmanagement.usermanagementsystem.entity.UserRole;
import com.stockmanagement.usermanagementsystem.service.UserService;
import com.stockmanagement.usermanagementsystem.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping
	public ResponseEntity<ApiResponse<UserResponse>> createUser(
			@Valid @RequestBody CreateUserRequest request,
			HttpServletRequest httpRequest) {

		String performedBy = getCurrentUser(httpRequest);
		UserResponse user = userService.createUser(request, performedBy);
		return ResponseUtil.created("User created successfully", user);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
		List<UserResponse> users = userService.getAllUsers();
		return ResponseUtil.success("Users retrieved successfully", users);
	}

	@GetMapping("/active")
	public ResponseEntity<ApiResponse<List<UserResponse>>> getActiveUsers() {
		List<UserResponse> users = userService.getActiveUsers();
		return ResponseUtil.success("Active users retrieved successfully", users);
	}

	@GetMapping("/inactive")
	public ResponseEntity<ApiResponse<List<UserResponse>>> getInactiveUsers() {
		List<UserResponse> users = userService.getInactiveUsers();
		return ResponseUtil.success("Inactive users retrieved successfully", users);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
		UserResponse user = userService.getUserById(id);
		return ResponseUtil.success("User retrieved successfully", user);
	}

	@GetMapping("/username/{username}")
	public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
		UserResponse user = userService.getUserByUsername(username);
		return ResponseUtil.success("User retrieved successfully", user);
	}

	@GetMapping("/role/{role}")
	public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable UserRole role) {
		List<UserResponse> users = userService.getUsersByRole(role);
		return ResponseUtil.success("Users retrieved successfully", users);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponse>> updateUser(
			@PathVariable Long id,
			@Valid @RequestBody UpdateUserRequest request,
			HttpServletRequest httpRequest) {

		String performedBy = getCurrentUser(httpRequest);
		UserResponse user = userService.updateUser(id, request, performedBy);
		return ResponseUtil.success("User updated successfully", user);
	}

	@PutMapping("/{id}/reset-password")
	public ResponseEntity<ApiResponse<UserResponse>> resetPassword(
			@PathVariable Long id,
			@Valid @RequestBody ResetPasswordRequest request,
			HttpServletRequest httpRequest) {

		String performedBy = getCurrentUser(httpRequest);
		UserResponse user = userService.resetPassword(id, request.getNewPassword(), performedBy);
		return ResponseUtil.success("Password reset successfully", user);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteUser(
			@PathVariable Long id,
			HttpServletRequest httpRequest) {

		String performedBy = getCurrentUser(httpRequest);
		userService.deleteUser(id, performedBy);
		return ResponseUtil.success("User deleted successfully", "User has been deactivated");
	}

	@GetMapping("/stats/count")
	public ResponseEntity<ApiResponse<Map<String, Long>>> getUserStats() {
		long activeCount = userService.getActiveUserCount();
		long adminCount = userService.getUserCountByRole(UserRole.ADMIN);
		long stockManagerCount = userService.getUserCountByRole(UserRole.STOCK_MANAGER);
		long salesStaffCount = userService.getUserCountByRole(UserRole.SALES_STAFF);
		long hrStaffCount = userService.getUserCountByRole(UserRole.HR_STAFF);
		long marketingManagerCount = userService.getUserCountByRole(UserRole.MARKETING_MANAGER);

		Map<String, Long> stats = Map.of(
				"activeUsers", activeCount,
				"admins", adminCount,
				"stockManagers", stockManagerCount,
				"salesStaff", salesStaffCount,
				"hrStaff", hrStaffCount,
				"marketingManagers", marketingManagerCount
		);

		return ResponseUtil.success("User statistics retrieved successfully", stats);
	}

	private String getCurrentUser(HttpServletRequest request) {
		// TODO: Implement proper authentication and get current user from security context
		// For now, return a default admin user
		return "admin";
	}
}
