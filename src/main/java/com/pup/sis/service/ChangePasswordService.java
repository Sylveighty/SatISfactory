package com.pup.sis.service;


import com.pup.sis.entity.User;
import com.pup.sis.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String changePassword(
            String username,
            String currentPassword,
            String newPassword,
            String confirmPassword) {

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return "Student number not found.";
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "Current password is incorrect.";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "New passwords do not match.";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "SUCCESS";
    }
} 
    

