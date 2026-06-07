package com.pup.sis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "faculty")
@Getter
@Setter
@NoArgsConstructor
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String facultyId;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(length = 100)
    private String department;

    // "Full Time" or "Part Time"
    @Column(length = 20)
    private String status;

    @Column(length = 20)
    private String mobileNumber;

    @Column(length = 100)
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}