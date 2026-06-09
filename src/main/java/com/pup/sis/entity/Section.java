package com.pup.sis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
@Getter
@Setter
@NoArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. "BSIT-SP 2-1"
    @Column(nullable = false, length = 50)
    private String sectionName;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // 1, 2, 3, or 4
    private Integer yearLevel;

    // Students assigned to this section
    // mappedBy points to the section field we'll add in Student
    @OneToMany(mappedBy = "section")
    private List<Student> students = new ArrayList<>();
}