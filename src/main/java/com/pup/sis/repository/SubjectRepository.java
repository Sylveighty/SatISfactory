package com.pup.sis.repository;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByCode(String code);

    // Used by the curriculum page to list all subjects for a given course
    List<Subject> findByCoursesContaining(Course course);

    // Used by the admin search bar on the subjects page
    List<Subject> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
            String name, String code);
}