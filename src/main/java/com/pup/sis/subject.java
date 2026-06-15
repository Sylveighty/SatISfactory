package com.pup.sis.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private Integer units;

    @Column(name = "lec_hours")
    private Integer lecHours;

    @Column(name = "lab_hours")
    private Integer labHours;

    @Column(name = "tuition_hours")
    private Integer tuitionHours;

    @ManyToMany
    @JoinTable(
        name = "subject_courses",
        joinColumns = @JoinColumn(name = "subject_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }

    public Integer getLecHours() { return lecHours; }
    public void setLecHours(Integer lecHours) { this.lecHours = lecHours; }

    public Integer getLabHours() { return labHours; }
    public void setLabHours(Integer labHours) { this.labHours = labHours; }

    public Integer getTuitionHours() { return tuitionHours; }
    public void setTuitionHours(Integer tuitionHours) { this.tuitionHours = tuitionHours; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
