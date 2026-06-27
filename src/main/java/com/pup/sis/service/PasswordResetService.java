package com.pup.sis.service;

import com.pup.sis.entity.PasswordResetToken;
import com.pup.sis.entity.User;
import com.pup.sis.repository.PasswordResetTokenRepository;
import com.pup.sis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.password-reset.token-expiry-minutes:30}")
    private int expiryMinutes;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Looks up a user by email, generates a reset token, and sends the link.
     * Always returns the same generic message regardless of whether the email
     * exists, to avoid leaking account information.
     */
    @Transactional
    public void requestReset(String email) {
        Optional<User> maybeUser = userRepository.findByEmail(email);
        if (maybeUser.isEmpty()) {
            // Silently do nothing - the caller shows a generic message either way
            return;
        }

        User user = maybeUser.get();

        // Remove any existing unused tokens for this user before issuing a new one
        tokenRepository.deleteByUser(user);

        String tokenValue = UUID.randomUUID().toString();

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        tokenRepository.save(token);

        sendResetEmail(user, tokenValue);
    }

    private void sendResetEmail(User user, String tokenValue) {
        String resetLink = baseUrl + "/reset-password?token=" + tokenValue;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("PUP SIS - Password Reset Request");
        message.setText(
                "Hello " + user.getFullName() + ",\n\n" +
                "We received a request to reset your PUP SIS password.\n\n" +
                "Click the link below to set a new password:\n" +
                resetLink + "\n\n" +
                "This link will expire in " + expiryMinutes + " minutes.\n\n" +
                "If you did not request a password reset, you can safely ignore this email.\n\n" +
                "- PUP SIS System"
        );

        mailSender.send(message);
    }

    /**
     * Validates the token and returns the associated user if valid.
     * Returns empty if the token is missing, expired, or already used.
     */
    public Optional<PasswordResetToken> validateToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .filter(t -> !t.isUsed())
                .filter(t -> !t.isExpired());
    }

    /**
     * Applies the new password and marks the token as used.
     * Returns an error message string on failure, null on success.
     */
    @Transactional
    public String resetPassword(String tokenValue, String newPassword, String confirmPassword) {
        Optional<PasswordResetToken> maybeToken = validateToken(tokenValue);

        if (maybeToken.isEmpty()) {
            return "This reset link is invalid or has expired. Please request a new one.";
        }

        if (newPassword == null || newPassword.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        if (!newPassword.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!newPassword.matches(".*[0-9].*")) {
            return "Password must contain at least one number.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        PasswordResetToken token = maybeToken.get();
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        return null;
    }
}