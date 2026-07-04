package com.stockmanagement.usermanagementsystem.config;

import com.stockmanagement.usermanagementsystem.entity.User;
import com.stockmanagement.usermanagementsystem.entity.UserRole;
import com.stockmanagement.usermanagementsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initialization component that creates default users on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");
        
        createDefaultUsers();
        
        logger.info("Data initialization completed successfully!");
    }

    /**
     * Create default users if they don't exist
     */
    private void createDefaultUsers() {
        // Create default admin user
        createUserIfNotExists(
            "admin", 
            "Admin@123", 
            UserRole.ADMIN, 
            "System Administrator"
        );

        // Create additional demo users for different roles
        createUserIfNotExists(
            "stockmanager", 
            "Stock@123", 
            UserRole.STOCK_MANAGER, 
            "Demo Stock Manager"
        );

        createUserIfNotExists(
            "salesstaff", 
            "Sales@123", 
            UserRole.SALES_STAFF, 
            "Demo Sales Staff"
        );

        createUserIfNotExists(
            "hrstaff", 
            "HR@123", 
            UserRole.HR_STAFF, 
            "Demo HR Staff"
        );

        createUserIfNotExists(
            "marketing", 
            "Marketing@123", 
            UserRole.MARKETING_MANAGER, 
            "Demo Marketing Manager"
        );
    }

    /**
     * Create a user if it doesn't already exist
     */
    private void createUserIfNotExists(String username, String password, UserRole role, String description) {
        try {
            // Check if user already exists
            if (userRepository.findByUsername(username).isPresent()) {
                logger.info("User '{}' already exists, skipping creation", username);
                return;
            }

            // Create new user
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setActive(true);
            user.setCreatedBy("SYSTEM");
            user.setUpdatedBy("SYSTEM");

            // Save user
            User savedUser = userRepository.save(user);
            
            logger.info("Created {} user: {} (ID: {})", 
                role.getDisplayName(), 
                savedUser.getUsername(), 
                savedUser.getId()
            );

        } catch (Exception e) {
            logger.error("Failed to create user '{}': {}", username, e.getMessage(), e);
        }
    }
}
