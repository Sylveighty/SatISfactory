package com.pup.sis.repository;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Grade;
import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Student's grades for a specific term - used by the student grades page
    List<Grade> findByStudentAndSchoolYearAndSemester(
            Student student, String schoolYear, String semester);

    // All grades for a section+subject in a term - used by faculty grade encoding
    List<Grade> findBySectionAndSubjectAndSchoolYearAndSemester(
            Section section, Subject subject,
            String schoolYear, String semester);

    // All grades a faculty member has submitted for a term
    List<Grade> findByFacultyAndSchoolYearAndSemester(
            Faculty faculty, String schoolYear, String semester);

    // Check if a specific grade record already exists
    Optional<Grade> findByStudentAndSubjectAndSchoolYearAndSemester(
            Student student, Subject subject,
            String schoolYear, String semester);
}