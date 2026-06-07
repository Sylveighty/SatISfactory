package com.pup.sis.config;

import com.pup.sis.entity.*;
import com.pup.sis.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDatabase(
            UserRepository userRepository,
            CourseRepository courseRepository,
            StudentRepository studentRepository,
            FacultyRepository facultyRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // ── Courses ───────────────────────────────────────────────
            if (courseRepository.count() == 0) {
                courseRepository.save(new Course("BSIT",
                        "Bachelor of Science in Information Technology"));
                courseRepository.save(new Course("BSCS",
                        "Bachelor of Science in Computer Science"));
                courseRepository.save(new Course("BSIS",
                        "Bachelor of Science in Information Systems"));
                System.out.println("✓ Courses seeded.");
            }

            // ── Users, Students, Faculty ──────────────────────────────
            if (userRepository.count() == 0) {

                Course bsit = courseRepository.findByCode("BSIT")
                        .orElseThrow(() -> new RuntimeException("BSIT course not found"));

                // ── Admin ─────────────────────────────────────────────
                User adminUser = buildUser(
                        "ADM-0001",
                        "admin123",
                        "ADMIN USER",
                        "admin@pup.edu.ph",
                        Role.ADMIN,
                        passwordEncoder);
                userRepository.save(adminUser);

                // ── Faculty (5) ───────────────────────────────────────
                seedFaculty(userRepository, facultyRepository, passwordEncoder,
                        "FAC-2024-001", "DELA CRUZ, JUAN BATUMBAKAL",
                        "Information Technology", "Full Time",
                        "0911-111-1111", "jb.delacruz@pup.edu.ph");

                seedFaculty(userRepository, facultyRepository, passwordEncoder,
                        "FAC-2024-002", "ESMASIN, JOHN CHRISTIAN CARTAGENA",
                        "Information Technology", "Full Time",
                        "0922-222-2222", "jc.esmasin@pup.edu.ph");

                seedFaculty(userRepository, facultyRepository, passwordEncoder,
                        "FAC-2024-003", "TIMBLACO, JOHN KENNETH LINA",
                        "Information Technology", "Part Time",
                        "0933-333-3333", "jk.timblaco@pup.edu.ph");

                seedFaculty(userRepository, facultyRepository, passwordEncoder,
                        "FAC-2024-004", "SORIANO, KARL ANGELO LABAY",
                        "Information Technology", "Full Time",
                        "0944-444-4444", "ka.soriano@pup.edu.ph");

                seedFaculty(userRepository, facultyRepository, passwordEncoder,
                        "FAC-2024-005", "REYES, MC JOBEN RODRIGUEZ",
                        "Information Technology", "Part Time",
                        "0955-555-5555", "mj.reyes@pup.edu.ph");

                System.out.println("✓ Faculty seeded.");

                // ── Students (10) ─────────────────────────────────────
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00001-MN-0", "VANCE, ALEXANDER",
                        bsit, 1, "Male", LocalDate.of(2006, 3, 15));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00002-MN-0", "ROSTOVA, ELENA",
                        bsit, 1, "Female", LocalDate.of(2006, 7, 22));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00003-MN-0", "ZHOU, MEI-LING",
                        bsit, 2, "Female", LocalDate.of(2005, 11, 3));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00004-MN-0", "ORTEGA, RAFAEL",
                        bsit, 3, "Male", LocalDate.of(2004, 5, 18));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00005-MN-0", "SANTOS, MARIA",
                        bsit, 2, "Female", LocalDate.of(2005, 1, 11));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00006-MN-0", "TANAKA, KENJI",
                        bsit, 1, "Male", LocalDate.of(2006, 9, 30));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00007-MN-0", "NAIR, ANANYA",
                        bsit, 2, "Female", LocalDate.of(2005, 4, 7));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00008-MN-0", "SILVA, MATEO",
                        bsit, 2, "Male", LocalDate.of(2005, 8, 25));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00009-MN-0", "HAYES, GENEVIEVE",
                        bsit, 4, "Female", LocalDate.of(2003, 12, 14));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00010-MN-0", "HADDAD, ZARA",
                        bsit, 3, "Female", LocalDate.of(2004, 6, 9));

                System.out.println("✓ Students seeded.");
                System.out.println("✓ Database seeding completed successfully.");
            }
        };
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User buildUser(
            String username,
            String rawPassword,
            String fullName,
            String email,
            Role role,
            PasswordEncoder encoder) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }

    private void seedFaculty(
            UserRepository userRepo,
            FacultyRepository facultyRepo,
            PasswordEncoder encoder,
            String facultyId,
            String fullName,
            String department,
            String status,
            String mobile,
            String email) {

        User user = buildUser(
                facultyId, "changeme", fullName, email, Role.FACULTY, encoder);
        userRepo.save(user);

        Faculty f = new Faculty();
        f.setFacultyId(facultyId);
        f.setFullName(fullName);
        f.setDepartment(department);
        f.setStatus(status);
        f.setMobileNumber(mobile);
        f.setEmail(email);
        f.setUser(user);
        facultyRepo.save(f);
    }

    private void seedStudent(
            UserRepository userRepo,
            StudentRepository studentRepo,
            PasswordEncoder encoder,
            String studentNumber,
            String fullName,
            Course course,
            Integer yearLevel,
            String gender,
            LocalDate dob) {

        User user = buildUser(
                studentNumber, "changeme", fullName, null, Role.STUDENT, encoder);
        userRepo.save(user);

        Student s = new Student();
        s.setStudentNumber(studentNumber);
        s.setFullName(fullName);
        s.setCourse(course);
        s.setYearLevel(yearLevel);
        s.setGender(gender);
        s.setDateOfBirth(dob);
        s.setUser(user);
        studentRepo.save(s);
    }
}