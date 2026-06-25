package com.pup.sis.service;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Grade;
import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.Subject;
import com.pup.sis.repository.GradeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public List<Grade> findByStudentAndTerm(
            Student student, String schoolYear, String semester) {
        return gradeRepository.findByStudentAndSchoolYearAndSemester(
                student, schoolYear, semester);
    }

    public List<Grade> findBySectionSubjectAndTerm(
            Section section, Subject subject,
            String schoolYear, String semester) {
        return gradeRepository.findBySectionAndSubjectAndSchoolYearAndSemester(
                section, subject, schoolYear, semester);
    }

    public List<Grade> findByFacultyAndTerm(
            Faculty faculty, String schoolYear, String semester) {
        return gradeRepository.findByFacultyAndSchoolYearAndSemester(
                faculty, schoolYear, semester);
    }

    public Optional<Grade> findByStudentSubjectAndTerm(
            Student student, Subject subject,
            String schoolYear, String semester) {
        return gradeRepository.findByStudentAndSubjectAndSchoolYearAndSemester(
                student, subject, schoolYear, semester);
    }

    /**
     * Calculates GPA from a list of grades.
     * Only numeric passing grades (1.0 to 3.0) are included.
     * Returns null if no gradeable records exist.
     */
    public Double calculateGPA(List<Grade> grades) {
        List<Double> numeric = grades.stream()
                .filter(g -> g.getFinalGrade() != null)
                .filter(g -> isNumericGrade(g.getFinalGrade()))
                .map(g -> Double.parseDouble(g.getFinalGrade()))
                .toList();

        if (numeric.isEmpty()) return null;

        return numeric.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Derives the grade status from the final grade string.
     * Used when faculty submits grades to auto-populate gradeStatus.
     */
    public String deriveStatus(String finalGrade) {
        if (finalGrade == null || finalGrade.isBlank()) return null;
        return switch (finalGrade) {
            case "INC" -> "Incomplete";
            case "DRP" -> "Dropped";
            case "5.0" -> "Failed";
            default    -> "Passed";
        };
    }

    public Grade save(Grade grade) {
        return gradeRepository.save(grade);
    }

    public void saveAll(List<Grade> grades) {
        gradeRepository.saveAll(grades);
    }

    public void delete(Long id) {
        gradeRepository.deleteById(id);
    }

    private boolean isNumericGrade(String grade) {
        try {
            double val = Double.parseDouble(grade);
            return val >= 1.0 && val <= 3.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}