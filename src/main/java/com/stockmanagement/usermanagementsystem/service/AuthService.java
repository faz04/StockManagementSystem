package com.stockmanagement.usermanagementsystem.service;

import com.stockmanagement.usermanagementsystem.dto.request.LoginRequest;
import com.stockmanagement.usermanagementsystem.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
	LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);

	void logout(String token);

	void logoutAllSessions(Long userId);

	LoginResponse validateToken(String token);

	boolean hasPermission(String token, String permission);

	boolean isAdmin(String token);

	void cleanupExpiredSessions();
}