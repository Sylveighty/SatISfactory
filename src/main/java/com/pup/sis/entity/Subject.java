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

    // Which courses offer this subject - Subject owns this relationship
    @ManyToMany
    @JoinTable(
        name = "subject_courses",
        joinColumns = @JoinColumn(name = "subject_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses = new ArrayList<>();
}