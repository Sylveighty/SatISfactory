package com.pup.sis.entity;

/**
 * Defines the three roles in the system.
 * Spring Security will prefix these with "ROLE_" automatically.
 */
public enum Role {
    ADMIN,
    FACULTY,
    STUDENT
}