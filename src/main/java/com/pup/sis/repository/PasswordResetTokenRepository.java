package com.pup.sis.repository;

import com.pup.sis.entity.PasswordResetToken;
import com.pup.sis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    // Used to invalidate any existing tokens before issuing a new one
    void deleteByUser(User user);
}