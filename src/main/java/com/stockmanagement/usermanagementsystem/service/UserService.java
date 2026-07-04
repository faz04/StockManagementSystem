package com.stockmanagement.usermanagementsystem.service;


import com.stockmanagement.usermanagementsystem.dto.request.CreateUserRequest;
import com.stockmanagement.usermanagementsystem.dto.request.UpdateUserRequest;
import com.stockmanagement.usermanagementsystem.dto.response.UserResponse;
import com.stockmanagement.usermanagementsystem.entity.UserRole;

import java.util.List;

public interface UserService {
	UserResponse createUser(CreateUserRequest request, String performedBy);

	UserResponse updateUser(Long userId, UpdateUserRequest request, String performedBy);

	void deleteUser(Long userId, String performedBy);

	UserResponse resetPassword(Long userId, String newPassword, String performedBy);

	List<UserResponse> getAllUsers();

	List<UserResponse> getActiveUsers();

	List<UserResponse> getInactiveUsers();

	UserResponse getUserById(Long id);

	UserResponse getUserByUsername(String username);

	List<UserResponse> getUsersByRole(UserRole role);

	long getActiveUserCount();

	long getUserCountByRole(UserRole role);
}