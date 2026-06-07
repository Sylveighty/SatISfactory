package com.pup.sis.repository;

import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentNumber(String studentNumber);

    // Used by StudentPortalController to load the profile
    // of whoever is currently logged in
    Optional<Student> findByUser(User user);

    // Used by the admin search bar
    List<Student> findByFullNameContainingIgnoreCase(String name);
}