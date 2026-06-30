package com.pup.sis.repository;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    // NOTE: facultyId is no longer unique at the DB level. Still safe here
    // (used by DataSeeder on a fresh DB where no duplicates exist yet). For
    // admin-facing duplicate checks, use findActiveByFacultyId below instead.
    Optional<Faculty> findByFacultyId(String facultyId);

    // Used to check if a faculty ID is currently held by an ACTIVE faculty
    // member. A deactivated faculty member sharing the same ID is ignored,
    // allowing the ID to be reused for a new record (e.g. correcting a
    // mis-encode).
    @Query("SELECT f FROM Faculty f WHERE f.facultyId = :facultyId " +
           "AND f.user IS NOT NULL AND f.user.enabled = true")
    Optional<Faculty> findActiveByFacultyId(@Param("facultyId") String facultyId);

    // Used by FacultyPortalController to load the profile
    // of whoever is currently logged in
    Optional<Faculty> findByUser(User user);

    // Used to filter only active (non-deactivated) faculty
    List<Faculty> findByUserEnabled(boolean enabled);
}