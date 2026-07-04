package com.stockmanagement.usermanagementsystem.controller;

import com.stockmanagement.usermanagementsystem.dto.request.LoginRequest;
import com.stockmanagement.usermanagementsystem.dto.response.ApiResponse;
import com.stockmanagement.usermanagementsystem.dto.response.LoginResponse;
import com.stockmanagement.usermanagementsystem.service.AuthService;
import com.stockmanagement.usermanagementsystem.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(
			@Valid @RequestBody LoginRequest request,
			HttpServletRequest httpRequest) {

		try {
			LoginResponse response = authService.login(request, httpRequest);
			return ResponseUtil.success("Login successful", response);
		} catch (Exception e) {
			return ResponseUtil.error(e.getMessage());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {

		try {
			String token = extractTokenFromHeader(authHeader);
			if (token != null) {
				authService.logout(token);
			}
			return ResponseUtil.success("Logged out successfully", "Session terminated");
		} catch (Exception e) {
			return ResponseUtil.error("Logout failed: " + e.getMessage());
		}
	}

	@GetMapping("/validate")
	public ResponseEntity<ApiResponse<LoginResponse>> validateToken(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {

		try {
			String token = extractTokenFromHeader(authHeader);
			if (token == null) {
				return ResponseUtil.error("No token provided");
			}

			LoginResponse response = authService.validateToken(token);
			return ResponseUtil.success("Token is valid", response);
		} catch (Exception e) {
			return ResponseUtil.error("Invalid token: " + e.getMessage());
		}
	}

	@PostMapping("/check-admin")
	public ResponseEntity<ApiResponse<Boolean>> checkAdminAccess(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {

		try {
			String token = extractTokenFromHeader(authHeader);
			if (token == null) {
				return ResponseUtil.success("Admin check completed", false);
			}

			boolean isAdmin = authService.isAdmin(token);
			return ResponseUtil.success("Admin check completed", isAdmin);
		} catch (Exception e) {
			return ResponseUtil.success("Admin check completed", false);
		}
	}

	@PostMapping("/check-permission")
	public ResponseEntity<ApiResponse<Boolean>> checkPermission(
			@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestParam String permission) {

		try {
			String token = extractTokenFromHeader(authHeader);
			if (token == null) {
				return ResponseUtil.success("Permission check completed", false);
			}

			boolean hasPermission = authService.hasPermission(token, permission);
			return ResponseUtil.success("Permission check completed", hasPermission);
		} catch (Exception e) {
			return ResponseUtil.success("Permission check completed", false);
		}
	}

	private String extractTokenFromHeader(String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}
}