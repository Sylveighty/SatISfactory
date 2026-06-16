package com.pup.sis.repository;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentNumber(String studentNumber);

    // Used by StudentPortalController to load the profile
    // of whoever is currently logged in
    Optional<Student> findByUser(User user);

    // Used by the admin search bar
    List<Student> findByFullNameContainingIgnoreCase(String name);

    // Used by section assignment to find eligible students
    List<Student> findByCourseAndYearLevel(Course course, Integer yearLevel);

    // Used by section delete to unassign students
    List<Student> findBySection(Section section);

    // Used by section cards to show student count
    long countBySection(Section section);
}