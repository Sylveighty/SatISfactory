package com.pup.sis.config;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Role;
import com.pup.sis.entity.User;
import com.pup.sis.repository.CourseRepository;
import com.pup.sis.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.function.Supplier;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDatabase(
            UserRepository userRepository,
            CourseRepository courseRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Prevent duplicate seeding
            if (userRepository.count() > 0 || courseRepository.count() > 0) {
                return;
            }

            // ─────────────────────────────────────────────
            // USERS
            // ─────────────────────────────────────────────
            userRepository.saveAll(List.of(
                    buildUser("admin",
                            "admin123",
                            "System Administrator",
                            "admin@pup.edu.ph",
                            Role.ADMIN,
                            passwordEncoder),

                    buildUser("faculty1",
                            "faculty123",
                            "Juan dela Cruz",
                            "jdelacruz@pup.edu.ph",
                            Role.FACULTY,
                            passwordEncoder),

                    buildUser("student1",
                            "student123",
                            "Maria Santos",
                            "msantos@pup.edu.ph",
                            Role.STUDENT,
                            passwordEncoder)
            ));

            // ─────────────────────────────────────────────
            // COURSES
            // ─────────────────────────────────────────────
            courseRepository.saveAll(List.of(
                    new Course("BSIT", "Bachelor of Science in Information Technology"),
                    new Course("BSCS", "Bachelor of Science in Computer Science"),
                    new Course("BSIS", "Bachelor of Science in Information Systems")
            ));

            System.out.println("✓ Database seeding completed successfully.");
        };
    }

    private User buildUser(
            String username,
            String rawPassword,
            String fullName,
            String email,
            Role role,
            PasswordEncoder encoder) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }
}