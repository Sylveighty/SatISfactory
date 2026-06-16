package com.pup.sis.repository;

import com.pup.sis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA gives us all basic CRUD methods for free.
 * We only need to declare the custom query for username lookup.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}