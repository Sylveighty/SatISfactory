package com.pup.sis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    // "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    @Column(nullable = false, length = 10)
    private String day;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(length = 50)
    private String room;

    // e.g. "2025-2026"
    @Column(nullable = false, length = 20)
    private String schoolYear;

    // "First Semester" or "Second Semester"
    @Column(nullable = false, length = 20)
    private String semester;
}