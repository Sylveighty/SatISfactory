package com.pup.sis.repository;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByFacultyId(String facultyId);

    // Used by FacultyPortalController to load the profile
    // of whoever is currently logged in
    Optional<Faculty> findByUser(User user);
}