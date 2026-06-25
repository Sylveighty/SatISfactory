package com.pup.sis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. "COMP 009"
    @Column(unique = true, nullable = false, length = 20)
    private String code;

    // e.g. "Object Oriented Programming"
    @Column(nullable = false, length = 150)
    private String name;

    private Integer units;

    private Double lecHours;

    private Double labHours;

    private Double tuitionHours;

    // Year level this subject is typically offered (1, 2, 3, or 4)
    // Nullable - allows subjects not tied to a specific year
    private Integer yearLevel;

    // Semester this subject is offered (1 or 2)
    // Nullable - allows subjects not tied to a specific semester
    private Integer semester;

    // Which courses offer this subject - Subject owns this relationship
    @ManyToMany
    @JoinTable(
        name = "subject_courses",
        joinColumns = @JoinColumn(name = "subject_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses = new ArrayList<>();
}