package com.pup.sis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Core user account entity. Shared across all roles.
 * Specific profile details (e.g. student number, department)
 * will be added as separate entities in later steps.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NOTE: unique constraint intentionally removed.
    // A deactivated student/faculty account may keep its old username
    // (their student/faculty number) on a disabled record, while a new
    // account reuses the same username after a mis-encode correction.
    // Login resolves to the ENABLED row only — see
    // UserRepository.findEnabledByUsername / CustomUserDetailsService.
    @Column(nullable = false, length = 50)
    private String username;

    // BCrypt-encoded password stored here
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, length = 100)
    private String email;

    // Stored as a string in the DB ("ADMIN", "FACULTY", "STUDENT")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Allows admins to disable accounts without deleting them
    @Column(nullable = false)
    private boolean enabled = true;
}