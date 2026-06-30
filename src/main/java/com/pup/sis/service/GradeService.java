package com.pup.sis.service;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Grade;
import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.Subject;
import com.pup.sis.repository.GradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Calculates GWA (General Weighted Average) from a list of grades,
     * weighted by each subject's unit count — matching how GWA is actually
     * computed (a 3-unit subject counts more than a 1-unit subject).
     *
     * Includes numeric grades from 1.0 to 5.0 (5.0 = failed is included,
     * since a failing grade should still pull the average up/down, the same
     * way it does in the real PUP grading system). Excludes non-numeric
     * statuses (INC, DRP) since those aren't final outcomes yet.
     *
     * Returns null if no gradeable records exist.
     */
    public Double calculateGPA(List<Grade> grades) {
        double weightedSum = 0.0;
        int totalUnits = 0;

        for (Grade g : grades) {
            if (g.getFinalGrade() == null || !isNumericGrade(g.getFinalGrade())) {
                continue;
            }
            int units = (g.getSubject() != null && g.getSubject().getUnits() != null)
                    ? g.getSubject().getUnits() : 0;
            if (units <= 0) {
                continue;
            }
            weightedSum += Double.parseDouble(g.getFinalGrade()) * units;
            totalUnits += units;
        }

        if (totalUnits == 0) return null;

        return weightedSum / totalUnits;
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

    @Transactional
    public Grade save(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Transactional
    public void saveAll(List<Grade> grades) {
        gradeRepository.saveAll(grades);
    }

    public void delete(Long id) {
        gradeRepository.deleteById(id);
    }

    private boolean isNumericGrade(String grade) {
        try {
            double val = Double.parseDouble(grade);
            // Valid PUP numeric grades range from 1.0 (highest) to 5.0 (failed).
            // INC and DRP are non-numeric and fail to parse here, which is correct.
            return val >= 1.0 && val <= 5.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}