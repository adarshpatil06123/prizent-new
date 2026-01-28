package com.elowen.identity.service;

import com.elowen.identity.entity.Client;
import com.elowen.identity.entity.User;
import com.elowen.identity.entity.Role;
import com.elowen.identity.repository.ClientRepository;
import com.elowen.identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataInitializationService implements ApplicationRunner {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeTestData();
    }

    private void initializeTestData() {
        // Check if test client already exists
        if (clientRepository.findByName("Test Client").isEmpty()) {
            // Create test client
            Client testClient = new Client();
            testClient.setName("Test Client");
            testClient.setNumberOfUsersAllowed(100);
            testClient.setLogo("test-logo.png");
            testClient.setEnabled(true);

            testClient = clientRepository.save(testClient);
            System.out.println("Created test client: " + testClient.getName());

            // Create test admin user
            User adminUser = new User();
            adminUser.setClientId(testClient.getId());
            adminUser.setName("Admin User");
            adminUser.setUsername("admin");
            adminUser.setEmailId("admin@test.com");
            adminUser.setPhoneNumber("+1234567890");
            adminUser.setEmployeeDesignation("Administrator");
            adminUser.setRole(Role.ADMIN);
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEnabled(true);

            userRepository.save(adminUser);
            System.out.println("Created test admin user: admin");

            // Create test regular user
            User regularUser = new User();
            regularUser.setClientId(testClient.getId());
            regularUser.setName("Test User");
            regularUser.setUsername("user");
            regularUser.setEmailId("user@test.com");
            regularUser.setPhoneNumber("+1234567891");
            regularUser.setEmployeeDesignation("Employee");
            regularUser.setRole(Role.USER);
            regularUser.setPassword(passwordEncoder.encode("user123"));
            regularUser.setEnabled(true);

            userRepository.save(regularUser);
            System.out.println("Created test regular user: user");
        } else {
            System.out.println("Test data already exists, skipping initialization");
        }
    }
}