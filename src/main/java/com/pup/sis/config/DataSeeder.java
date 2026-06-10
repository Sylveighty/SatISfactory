package com.pup.sis.config;

import com.pup.sis.entity.*;
import com.pup.sis.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDatabase(
            UserRepository userRepository,
            CourseRepository courseRepository,
            StudentRepository studentRepository,
            FacultyRepository facultyRepository,
            SubjectRepository subjectRepository,
            SectionRepository sectionRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Initialize core course programs if not already seeded
            if (courseRepository.count() == 0) {
                // Create the main computer science degree programs offered by the institution
                courseRepository.save(new Course("BSIT",
                        "Bachelor of Science in Information Technology"));
                courseRepository.save(new Course("BSCS",
                        "Bachelor of Science in Computer Science"));
                courseRepository.save(new Course("BSIS",
                        "Bachelor of Science in Information Systems"));
                System.out.println("✓ Courses seeded.");
            }

            // Initialize user accounts (admin, faculty, and students) if not already seeded
            if (userRepository.count() == 0) {

                // Retrieve the BSIT course for student assignments
                Course bsit = courseRepository.findByCode("BSIT")
                        .orElseThrow(() -> new RuntimeException("BSIT not found"));

                // Create the admin user account with system-level permissions
                userRepository.save(buildUser(
                        "ADM-0001", "admin123", "ADMIN USER",
                        "admin@pup.edu.ph", Role.ADMIN, passwordEncoder));

                // Create 5 faculty members for the Information Technology department
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

                // ── Students ──────────────────────────────────────────
                // Year enrolled is reflected in the student number.
                // Format: YYYY-NNNNN-SP-0 (SP = San Pedro, 0 = Regular)

                // Year 1 - enrolled 2026
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2026-00001-SP-0", "VANCE, ALEXANDER",
                        bsit, 1, "Male", LocalDate.of(2007, 3, 15));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2026-00002-SP-0", "ROSTOVA, ELENA",
                        bsit, 1, "Female", LocalDate.of(2007, 7, 22));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2026-00003-SP-0", "TANAKA, KENJI",
                        bsit, 1, "Male", LocalDate.of(2007, 9, 30));

                // Create Year 2 students (enrolled in 2025)
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2025-00001-SP-0", "ZHOU, MEI-LING",
                        bsit, 2, "Female", LocalDate.of(2006, 11, 3));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2025-00002-SP-0", "SANTOS, MARIA",
                        bsit, 2, "Female", LocalDate.of(2006, 1, 11));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2025-00003-SP-0", "NAIR, ANANYA",
                        bsit, 2, "Female", LocalDate.of(2006, 4, 7));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2025-00004-SP-0", "SILVA, MATEO",
                        bsit, 2, "Male", LocalDate.of(2006, 8, 25));

                // Create Year 3 students (enrolled in 2024)
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00001-SP-0", "ORTEGA, RAFAEL",
                        bsit, 3, "Male", LocalDate.of(2005, 5, 18));

                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00002-SP-0", "HADDAD, ZARA",
                        bsit, 3, "Female", LocalDate.of(2005, 6, 9));

                // Create Year 4 students (enrolled in 2023 - final year)
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2023-00001-SP-0", "HAYES, GENEVIEVE",
                        bsit, 4, "Female", LocalDate.of(2004, 12, 14));

                System.out.println("✓ Students seeded.");
                System.out.println("✓ Users seeded successfully.");
            }

            // Initialize curriculum subjects (courses) for all degree programs
            if (subjectRepository.count() == 0) {

                // Retrieve the BSIT program to associate with subjects
                Course bsit = courseRepository.findByCode("BSIT")
                        .orElseThrow(() -> new RuntimeException("BSIT not found"));

                List<Course> bsitOnly = List.of(bsit);

                // Define Year 1 subjects in the curriculum
                subjectRepository.save(buildSubject("COMP 002",
                        "Computer Programming 1",
                        3, 2.0, 3.0, 5.0, bsitOnly));

                // Define Year 2 subjects in the curriculum
                subjectRepository.save(buildSubject("COMP 009",
                        "Object Oriented Programming",
                        3, 2.0, 3.0, 5.0, bsitOnly));

                subjectRepository.save(buildSubject("COMP 010",
                        "Information Management",
                        3, 2.0, 3.0, 5.0, bsitOnly));

                subjectRepository.save(buildSubject("COMP 012",
                        "Network Administration",
                        3, 2.0, 3.0, 5.0, bsitOnly));

                subjectRepository.save(buildSubject("COMP 013",
                        "Human Computer Interaction",
                        3, 3.0, 0.0, 3.0, bsitOnly));

                subjectRepository.save(buildSubject("COMP 014",
                        "Quantitative Methods with Modeling and Simulation",
                        3, 3.0, 0.0, 3.0, bsitOnly));

                subjectRepository.save(buildSubject("ELEC IT-FE2",
                        "BSIT Free Elective 2",
                        3, 3.0, 0.0, 3.0, bsitOnly));

                subjectRepository.save(buildSubject("INTE 202",
                        "Integrative Programming and Technologies 1",
                        3, 2.0, 3.0, 5.0, bsitOnly));

                subjectRepository.save(buildSubject("PATHFIT 4",
                        "Physical Activity Towards Health and Fitness 4",
                        2, 2.0, 0.0, 2.0, bsitOnly));

                // Define Year 3 subjects in the curriculum
                subjectRepository.save(buildSubject("COMP 018",
                        "Database Administration",
                        3, 2.0, 3.0, 5.0, bsitOnly));

                // Define Year 4 subjects in the curriculum
                subjectRepository.save(buildSubject("COMP 023",
                        "Social and Professional Issues in Computing",
                        3, 3.0, 0.0, 3.0, bsitOnly));

                System.out.println("✓ Subjects seeded.");
            }

            // Initialize class sections and assign students to their respective sections
            if (sectionRepository.count() == 0) {

                // Retrieve the BSIT course for section creation
                Course bsit = courseRepository.findByCode("BSIT")
                        .orElseThrow(() -> new RuntimeException("BSIT not found"));

                // Create section groups for each year level (2 sections per year for load balancing)
                Section bsit1_1 = sectionRepository.save(
                        buildSection("BSIT-SP 1-1", bsit, 1));
                Section bsit1_2 = sectionRepository.save(
                        buildSection("BSIT-SP 1-2", bsit, 1));
                Section bsit2_1 = sectionRepository.save(
                        buildSection("BSIT-SP 2-1", bsit, 2));
                Section bsit2_2 = sectionRepository.save(
                        buildSection("BSIT-SP 2-2", bsit, 2));
                Section bsit3_1 = sectionRepository.save(
                        buildSection("BSIT-SP 3-1", bsit, 3));
                Section bsit4_1 = sectionRepository.save(
                        buildSection("BSIT-SP 4-1", bsit, 4));

                // Assign Year 1 students to their respective sections
                assignSection(studentRepository, "2026-00001-SP-0", bsit1_1);
                assignSection(studentRepository, "2026-00002-SP-0", bsit1_1);
                assignSection(studentRepository, "2026-00003-SP-0", bsit1_2);

                // Assign Year 2 students to their respective sections
                assignSection(studentRepository, "2025-00001-SP-0", bsit2_1);
                assignSection(studentRepository, "2025-00002-SP-0", bsit2_1);
                assignSection(studentRepository, "2025-00003-SP-0", bsit2_2);
                assignSection(studentRepository, "2025-00004-SP-0", bsit2_2);

                // Assign Year 3 students to their respective sections
                assignSection(studentRepository, "2024-00001-SP-0", bsit3_1);
                assignSection(studentRepository, "2024-00002-SP-0", bsit3_1);

                // Assign Year 4 student to their section
                assignSection(studentRepository, "2023-00001-SP-0", bsit4_1);

                System.out.println("✓ Sections seeded and students assigned.");
                System.out.println("✓ Database seeding completed successfully.");
            }
        };
    }

    // Helper methods for building and seeding data
    // These methods provide reusable logic for creating and saving entities

    // Create a user account with the specified role and credentials
    private User buildUser(
            String username, String rawPassword, String fullName,
            String email, Role role, PasswordEncoder encoder) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword)); // Passwords are encrypted for security
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role); // Assign the user's access level
        user.setEnabled(true); // Activate the account
        return user;
    }

    // Create and save a faculty member profile with associated user account
    private void seedFaculty(
            UserRepository userRepo, FacultyRepository facultyRepo,
            PasswordEncoder encoder, String facultyId, String fullName,
            String department, String status, String mobile, String email) {

        // Create the login account using the faculty ID as the username
        User user = buildUser(
                facultyId, "changeme", fullName, email, Role.FACULTY, encoder);
        userRepo.save(user);

        // Create the faculty profile linked to the user account
        Faculty f = new Faculty();
        f.setFacultyId(facultyId);
        f.setFullName(fullName);
        f.setDepartment(department);
        f.setStatus(status); // Full Time or Part Time
        f.setMobileNumber(mobile);
        f.setEmail(email);
        f.setUser(user);
        facultyRepo.save(f);
    }

    // Create and save a student profile with associated user account
    private void seedStudent(
            UserRepository userRepo, StudentRepository studentRepo,
            PasswordEncoder encoder, String studentNumber, String fullName,
            Course course, Integer yearLevel, String gender, LocalDate dob) {

        // Create the login account using the student number as the username
        User user = buildUser(
                studentNumber, "changeme", fullName, null, Role.STUDENT, encoder);
        userRepo.save(user);

        // Create the student profile linked to the user account
        Student s = new Student();
        s.setStudentNumber(studentNumber);
        s.setFullName(fullName);
        s.setCourse(course); // The student's degree program
        s.setYearLevel(yearLevel); // Current academic year (1-4)
        s.setGender(gender);
        s.setDateOfBirth(dob);
        s.setUser(user);
        studentRepo.save(s);
    }

    private Subject buildSubject(
            String code, String name, int units,
            double lec, double lab, double tuition,
            List<Course> courses) {

        Subject s = new Subject();
        s.setCode(code);
        s.setName(name);
        s.setUnits(units);
        s.setLecHours(lec);
        s.setLabHours(lab);
        s.setTuitionHours(tuition);
        s.setCourses(new java.util.ArrayList<>(courses));
        return s;
    }

    private Section buildSection(
            String sectionName, Course course, int yearLevel) {

        Section s = new Section();
        s.setSectionName(sectionName);
        s.setCourse(course);
        s.setYearLevel(yearLevel);
        return s;
    }

    private void assignSection(
            StudentRepository studentRepo,
            String studentNumber, Section section) {

        studentRepo.findByStudentNumber(studentNumber).ifPresent(student -> {
            student.setSection(section);
            studentRepo.save(student);
        });
    }
}