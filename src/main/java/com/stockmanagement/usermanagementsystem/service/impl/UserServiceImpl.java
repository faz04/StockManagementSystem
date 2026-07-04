package com.stockmanagement.usermanagementsystem.service.impl;


import com.stockmanagement.usermanagementsystem.dto.request.CreateUserRequest;
import com.stockmanagement.usermanagementsystem.dto.request.UpdateUserRequest;
import com.stockmanagement.usermanagementsystem.dto.response.UserResponse;
import com.stockmanagement.usermanagementsystem.entity.User;
import com.stockmanagement.usermanagementsystem.entity.UserRole;
import com.stockmanagement.usermanagementsystem.exception.UserAlreadyExistsException;
import com.stockmanagement.usermanagementsystem.exception.UserNotFoundException;
import com.stockmanagement.usermanagementsystem.repository.UserRepository;
import com.stockmanagement.usermanagementsystem.service.AuditLogService;
import com.stockmanagement.usermanagementsystem.service.UserService;
import com.stockmanagement.usermanagementsystem.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PasswordValidator passwordValidator;

	@Override
	public UserResponse createUser(CreateUserRequest request, String performedBy) {
		// Validate username doesn't exist
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' already exists");
		}

		// Validate password policy
		passwordValidator.validatePassword(request.getPassword());

		// Create user
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole());
		user.setActive(true);
		user.setCreatedBy(performedBy);
		user.setUpdatedBy(performedBy);

		User savedUser = userRepository.save(user);

		// Log activity
		auditLogService.logActivity("CREATE_USER", performedBy, savedUser.getUsername(),
				"User created with role: " + savedUser.getRole().getDisplayName());

		return convertToResponse(savedUser);
	}

	@Override
	public UserResponse updateUser(Long userId, UpdateUserRequest request, String performedBy) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		StringBuilder changes = new StringBuilder();

		// Update username if provided and different
		if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
			if (userRepository.existsByUsername(request.getUsername())) {
				throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' already exists");
			}
			changes.append("Username changed from ").append(user.getUsername())
					.append(" to ").append(request.getUsername()).append("; ");
			user.setUsername(request.getUsername());
		}

		// Update role if provided and different
		if (request.getRole() != null && !request.getRole().equals(user.getRole())) {
			changes.append("Role changed from ").append(user.getRole().getDisplayName())
					.append(" to ").append(request.getRole().getDisplayName()).append("; ");
			user.setRole(request.getRole());
		}

		// Update active status if provided and different
		if (request.getActive() != null && !request.getActive().equals(user.isActive())) {
			changes.append("Status changed to ").append(request.getActive() ? "Active" : "Inactive").append("; ");
			user.setActive(request.getActive());
		}

		user.setUpdatedBy(performedBy);
		User savedUser = userRepository.save(user);

		// Log activity
		if (changes.length() > 0) {
			auditLogService.logActivity("UPDATE_USER", performedBy, savedUser.getUsername(),
					changes.toString());
		}

		return convertToResponse(savedUser);
	}

	@Override
	public void deleteUser(Long userId, String performedBy) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		// Soft delete by setting active to false
		user.setActive(false);
		user.setUpdatedBy(performedBy);
		userRepository.save(user);

		// Log activity
		auditLogService.logActivity("DELETE_USER", performedBy, user.getUsername(),
				"User account deactivated");
	}

	@Override
	public UserResponse resetPassword(Long userId, String newPassword, String performedBy) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		// Validate password policy
		passwordValidator.validatePassword(newPassword);

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setUpdatedBy(performedBy);
		User savedUser = userRepository.save(user);

		// Log activity
		auditLogService.logActivity("RESET_PASSWORD", performedBy, savedUser.getUsername(),
				"Password reset for user");

		return convertToResponse(savedUser);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getActiveUsers() {
		return userRepository.findByActiveTrue().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getInactiveUsers() {
		return userRepository.findByActiveFalse().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
		return convertToResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
		return convertToResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getUsersByRole(UserRole role) {
		return userRepository.findByRole(role).stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public long getActiveUserCount() {
		return userRepository.countActiveUsers();
	}

	@Override
	@Transactional(readOnly = true)
	public long getUserCountByRole(UserRole role) {
		return userRepository.countByRole(role);
	}

	private UserResponse convertToResponse(User user) {
		UserResponse response = new UserResponse();
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setRole(user.getRole());
		response.setActive(user.isActive());
		response.setCreatedAt(user.getCreatedAt());
		response.setUpdatedAt(user.getUpdatedAt());
		response.setCreatedBy(user.getCreatedBy());
		response.setUpdatedBy(user.getUpdatedBy());
		return response;
	}
}
