package com.pup.sis.service;

import com.pup.sis.entity.User;
import com.pup.sis.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer sits between controllers and the repository.
 * Keeps controllers thin and business logic testable.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}