package com.pup.sis.service;

import com.pup.sis.entity.User;
import com.pup.sis.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Validates the current password, checks confirmation match,
     * enforces password rules, then saves the new BCrypt-encoded password.
     *
     * Returns null on success, or an error message string on failure.
     */
    public String changePassword(User user, String currentPassword,
                                 String newPassword, String confirmPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "Current password is incorrect.";
        }
        if (newPassword == null || newPassword.length() < 8) {
            return "New password must be at least 8 characters.";
        }
        if (!newPassword.matches(".*[A-Z].*")) {
            return "New password must contain at least one uppercase letter.";
        }
        if (!newPassword.matches(".*[0-9].*")) {
            return "New password must contain at least one number.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            return "New password must be different from the current password.";
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return null;
    }
}