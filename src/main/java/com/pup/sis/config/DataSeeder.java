package com.pup.sis.config;

import com.pup.sis.entity.Role;
import com.pup.sis.entity.User;
import com.pup.sis.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Runs once on startup.
 * Creates the three default accounts only if the users table is empty,
 * so it won't duplicate on every restart.
 */
@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (userRepository.count() > 0) {
                return; // Already seeded — skip
            }

            // ── Admin ──────────────────────────────────────────────────────
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setEmail("admin@pup.edu.ph");
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);

            // ── Faculty ────────────────────────────────────────────────────
            User faculty = new User();
            faculty.setUsername("faculty1");
            faculty.setPassword(passwordEncoder.encode("faculty123"));
            faculty.setFullName("Juan dela Cruz");
            faculty.setEmail("jdelacruz@pup.edu.ph");
            faculty.setRole(Role.FACULTY);
            faculty.setEnabled(true);
            userRepository.save(faculty);

            // ── Student ────────────────────────────────────────────────────
            User student = new User();
            student.setUsername("student1");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setFullName("Maria Santos");
            student.setEmail("msantos@pup.edu.ph");
            student.setRole(Role.STUDENT);
            student.setEnabled(true);
            userRepository.save(student);

            System.out.println("✓ Default users seeded successfully.");
        };
    }
}