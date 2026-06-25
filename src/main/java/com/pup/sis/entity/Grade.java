package com.pup.sis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "grades",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"student_id", "subject_id", "school_year", "semester"}
    )
)
@Getter
@Setter
@NoArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    // e.g. "2025-2026"
    @Column(nullable = false, length = 20)
    private String schoolYear;

    // "First Semester" or "Second Semester"
    @Column(nullable = false, length = 20)
    private String semester;

    // "1.0" / "1.25" / "1.5" / "1.75" / "2.0" / "2.25" /
    // "2.5" / "2.75" / "3.0" / "5.0" / "INC" / "DRP"
    @Column(length = 10)
    private String finalGrade;

    // "Passed" / "Failed" / "Incomplete" / "Dropped"
    @Column(length = 20)
    private String gradeStatus;
}