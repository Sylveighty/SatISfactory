package com.pup.sis.repository;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // NOTE: now that studentNumber is no longer unique at the DB level,
    // this can technically match more than one row (e.g. one deactivated,
    // one active). Left as-is (still used by DataSeeder on a fresh DB,
    // where no duplicates exist yet). For admin-facing duplicate checks,
    // use findActiveByStudentNumber below instead.
    Optional<Student> findByStudentNumber(String studentNumber);

    // Used to check if a student number is currently held by an ACTIVE student.
    // A deactivated student sharing the same number is ignored, allowing the
    // number to be reused for a new record (e.g. correcting a mis-encode).
    @Query("SELECT s FROM Student s WHERE s.studentNumber = :studentNumber " +
           "AND s.user IS NOT NULL AND s.user.enabled = true")
    Optional<Student> findActiveByStudentNumber(@Param("studentNumber") String studentNumber);

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

    // Used to filter only active (non-deactivated) students
    List<Student> findByUserEnabled(boolean enabled);
}