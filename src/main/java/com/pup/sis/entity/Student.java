package com.pup.sis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NOTE: unique constraint intentionally removed.
    // A deactivated (soft-deleted) student may keep their old studentNumber
    // on a disabled record, while a new student is created with the same
    // number (e.g. after a mis-encode). Uniqueness among ACTIVE students is
    // enforced in StudentService instead of at the DB level.
    @Column(nullable = false, length = 30)
    private String studentNumber;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(length = 10)
    private String gender;

    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String placeOfBirth;

    @Column(length = 20)
    private String mobileNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 250)
    private String residentialAddress;

    @Column(length = 250)
    private String permanentAddress;

    @Column(length = 100)
    private String spouseName;

    // Stored as integer: 1, 2, 3, 4
    private Integer yearLevel;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // Which section this student is assigned to (nullable - set by admin)
    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}