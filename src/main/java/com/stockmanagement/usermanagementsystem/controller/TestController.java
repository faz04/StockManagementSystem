package com.stockmanagement.usermanagementsystem.controller;



import com.stockmanagement.usermanagementsystem.dto.response.ApiResponse;
import com.stockmanagement.usermanagementsystem.entity.User;
import com.stockmanagement.usermanagementsystem.entity.UserRole;
import com.stockmanagement.usermanagementsystem.repository.UserRepository;
import com.stockmanagement.usermanagementsystem.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        return ResponseUtil.success("Hello from Stock Management System!");
    }

    @GetMapping("/database")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testDatabase() {
        try {
            // Create a test user if none exists
            if (userRepository.count() == 0) {
                User testUser = new User("admin", passwordEncoder.encode("Admin@123"), UserRole.ADMIN, "system");
                userRepository.save(testUser);
            }

            long userCount = userRepository.count();
            long activeUserCount = userRepository.countActiveUsers();

            Map<String, Object> result = Map.of(
                    "message", "Database connection successful!",
                    "totalUsers", userCount,
                    "activeUsers", activeUserCount
            );

            return ResponseUtil.success("Database test completed successfully", result);
        } catch (Exception e) {
            return ResponseUtil.error("Database connection failed: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseUtil.success("Users retrieved successfully", users);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<ApiResponse<String>> createDefaultAdmin() {
        try {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", passwordEncoder.encode("Admin@123"), UserRole.ADMIN, "system");
                userRepository.save(admin);
                return ResponseUtil.success("Default admin user created successfully",
                        "Username: admin, Password: Admin@123");
            } else {
                return ResponseUtil.success("Admin user already exists", "No action needed");
            }
        } catch (Exception e) {
            return ResponseUtil.error("Failed to create admin user: " + e.getMessage());
        }
    }
}