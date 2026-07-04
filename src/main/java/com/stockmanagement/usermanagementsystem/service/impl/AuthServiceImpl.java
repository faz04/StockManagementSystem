package com.stockmanagement.usermanagementsystem.service.impl;

import com.stockmanagement.usermanagementsystem.dto.request.LoginRequest;
import com.stockmanagement.usermanagementsystem.dto.response.LoginResponse;
import com.stockmanagement.usermanagementsystem.entity.User;
import com.stockmanagement.usermanagementsystem.entity.UserSession;
import com.stockmanagement.usermanagementsystem.exception.UserNotFoundException;
import com.stockmanagement.usermanagementsystem.repository.UserRepository;
import com.stockmanagement.usermanagementsystem.repository.UserSessionRepository;
import com.stockmanagement.usermanagementsystem.service.AuditLogService;
import com.stockmanagement.usermanagementsystem.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false) // Make it optional since you might not have implemented it yet
    private AuditLogService auditLogService;

    @Override
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // Find user by username
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            if (auditLogService != null) {
                auditLogService.logActivity("LOGIN_FAILED", request.getUsername(), request.getUsername(),
                        "User not found", getClientIpAddress(httpRequest));
            }
            throw new UserNotFoundException("Invalid username or password");
        }

        User user = userOpt.get();

        // Check if user is active
        if (!user.isActive()) {
            if (auditLogService != null) {
                auditLogService.logActivity("LOGIN_FAILED", request.getUsername(), user.getUsername(),
                        "Account is deactivated", getClientIpAddress(httpRequest));
            }
            throw new RuntimeException("Account is deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            if (auditLogService != null) {
                auditLogService.logActivity("LOGIN_FAILED", request.getUsername(), user.getUsername(),
                        "Invalid password", getClientIpAddress(httpRequest));
            }
            throw new RuntimeException("Invalid username or password");
        }

        // Generate session token
        String token = generateToken();
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(8); // 8 hour session

        // Create session
        UserSession session = new UserSession(user.getId(), token, expiryTime);
        session.setIpAddress(getClientIpAddress(httpRequest));

        sessionRepository.save(session);

        // Log successful login
        if (auditLogService != null) {
            auditLogService.logActivity("LOGIN_SUCCESS", user.getUsername(), user.getUsername(),
                    "User logged in successfully", getClientIpAddress(httpRequest));
        }

        // Create response
        LoginResponse response = new LoginResponse(user.getId(), user.getUsername(), user.getRole(), token);
        return response;
    }

    @Override
    public void logout(String token) {
        Optional<UserSession> sessionOpt = sessionRepository.findByTokenAndActiveTrue(token);
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            sessionRepository.deactivateSession(token);

            // Get user for audit log
            Optional<User> userOpt = userRepository.findById(session.getUserId());
            if (userOpt.isPresent() && auditLogService != null) {
                auditLogService.logActivity("LOGOUT", userOpt.get().getUsername(),
                        userOpt.get().getUsername(), "User logged out", session.getIpAddress());
            }
        }
    }

    @Override
    public void logoutAllSessions(Long userId) {
        // Deactivate all active sessions for the user
        sessionRepository.deactivateAllUserSessions(userId);

        // Get user for audit log
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent() && auditLogService != null) {
            auditLogService.logActivity("LOGOUT_ALL", userOpt.get().getUsername(),
                    userOpt.get().getUsername(), "All sessions logged out", null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse validateToken(String token) {
        Optional<UserSession> sessionOpt = sessionRepository.findByTokenAndActiveTrue(token);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Invalid or expired token");
        }

        UserSession session = sessionOpt.get();

        // Check if session is expired
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            sessionRepository.deactivateSession(token);
            throw new RuntimeException("Session expired");
        }

        // Get user
        Optional<User> userOpt = userRepository.findById(session.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().isActive()) {
            sessionRepository.deactivateSession(token);
            throw new RuntimeException("User account is deactivated");
        }

        User user = userOpt.get();
        return new LoginResponse(user.getId(), user.getUsername(), user.getRole(), token);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(String token, String permission) {
        try {
            LoginResponse loginResponse = validateToken(token);
            return loginResponse.getPermissions().contains(permission);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin(String token) {
        try {
            LoginResponse loginResponse = validateToken(token);
            return loginResponse.isAdmin();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void cleanupExpiredSessions() {
        sessionRepository.deactivateExpiredSessions(LocalDateTime.now());
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "") +
                UUID.randomUUID().toString().replace("-", "");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            return xForwardedForHeader.split(",")[0].trim();
        }

        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (xRealIpHeader != null && !xRealIpHeader.isEmpty()) {
            return xRealIpHeader;
        }

        return request.getRemoteAddr();
    }
}