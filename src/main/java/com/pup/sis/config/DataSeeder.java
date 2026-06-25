package com.pup.sis.config;

import com.pup.sis.entity.*;
import com.pup.sis.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataSeeder {

    // Current academic term - update each semester
    static final String CURRENT_YEAR = "2025-2026";
    static final String CURRENT_SEM  = "Second Semester";

    @Bean
    public CommandLineRunner seedDatabase(
            UserRepository userRepository,
            CourseRepository courseRepository,
            StudentRepository studentRepository,
            FacultyRepository facultyRepository,
            SubjectRepository subjectRepository,
            SectionRepository sectionRepository,
            ScheduleRepository scheduleRepository,
            GradeRepository gradeRepository,
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
                        .orElseThrow(() -> new RuntimeException("BSIT not found"));

                // Admin
                userRepository.save(buildUser("ADM-0001", "admin123",
                        "ADMIN USER", "admin@pup.edu.ph", Role.ADMIN, passwordEncoder));

                // Faculty
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

                // Students
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2026-00001-SP-0", "VANCE, ALEXANDER",
                        bsit, 1, "Male", LocalDate.of(2007, 3, 15));
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2026-00002-SP-0", "ROSTOVA, ELENA",
                        bsit, 1, "Female", LocalDate.of(2007, 7, 22));
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2026-00003-SP-0", "TANAKA, KENJI",
                        bsit, 1, "Male", LocalDate.of(2007, 9, 30));
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
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00001-SP-0", "ORTEGA, RAFAEL",
                        bsit, 3, "Male", LocalDate.of(2005, 5, 18));
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2024-00002-SP-0", "HADDAD, ZARA",
                        bsit, 3, "Female", LocalDate.of(2005, 6, 9));
                seedStudent(userRepository, studentRepository, passwordEncoder,
                        "2023-00001-SP-0", "HAYES, GENEVIEVE",
                        bsit, 4, "Female", LocalDate.of(2004, 12, 14));
                System.out.println("✓ Students seeded.");
            }

            // ── Subjects ──────────────────────────────────────────────
            if (subjectRepository.count() == 0) {
                Course bsit = courseRepository.findByCode("BSIT")
                        .orElseThrow(() -> new RuntimeException("BSIT not found"));
                List<Course> bsitOnly = List.of(bsit);

                subjectRepository.save(buildSubject("COMP 002",
                        "Computer Programming 1",
                        3, 2.0, 3.0, 5.0, 1, 1, bsitOnly));
                subjectRepository.save(buildSubject("COMP 009",
                        "Object Oriented Programming",
                        3, 2.0, 3.0, 5.0, 2, 1, bsitOnly));
                subjectRepository.save(buildSubject("COMP 010",
                        "Information Management",
                        3, 2.0, 3.0, 5.0, 2, 1, bsitOnly));
                subjectRepository.save(buildSubject("COMP 012",
                        "Network Administration",
                        3, 2.0, 3.0, 5.0, 2, 2, bsitOnly));
                subjectRepository.save(buildSubject("COMP 013",
                        "Human Computer Interaction",
                        3, 3.0, 0.0, 3.0, 2, 1, bsitOnly));
                subjectRepository.save(buildSubject("COMP 014",
                        "Quantitative Methods with Modeling and Simulation",
                        3, 3.0, 0.0, 3.0, 2, 2, bsitOnly));
                subjectRepository.save(buildSubject("ELEC IT-FE2",
                        "BSIT Free Elective 2",
                        3, 3.0, 0.0, 3.0, 2, 2, bsitOnly));
                subjectRepository.save(buildSubject("INTE 202",
                        "Integrative Programming and Technologies 1",
                        3, 2.0, 3.0, 5.0, 2, 2, bsitOnly));
                subjectRepository.save(buildSubject("PATHFIT 4",
                        "Physical Activity Towards Health and Fitness 4",
                        2, 2.0, 0.0, 2.0, 2, 2, bsitOnly));
                subjectRepository.save(buildSubject("COMP 018",
                        "Database Administration",
                        3, 2.0, 3.0, 5.0, 3, 1, bsitOnly));
                subjectRepository.save(buildSubject("COMP 023",
                        "Social and Professional Issues in Computing",
                        3, 3.0, 0.0, 3.0, 4, 1, bsitOnly));
                System.out.println("✓ Subjects seeded.");
            } else {
                // Update year level and semester on existing subjects
                // Safe to run every time - only updates if field is null
                updateSubjectMeta(subjectRepository, "COMP 002", 1, 1);
                updateSubjectMeta(subjectRepository, "COMP 009", 2, 1);
                updateSubjectMeta(subjectRepository, "COMP 010", 2, 1);
                updateSubjectMeta(subjectRepository, "COMP 012", 2, 2);
                updateSubjectMeta(subjectRepository, "COMP 013", 2, 1);
                updateSubjectMeta(subjectRepository, "COMP 014", 2, 2);
                updateSubjectMeta(subjectRepository, "ELEC IT-FE2", 2, 2);
                updateSubjectMeta(subjectRepository, "INTE 202", 2, 2);
                updateSubjectMeta(subjectRepository, "PATHFIT 4", 2, 2);
                updateSubjectMeta(subjectRepository, "COMP 018", 3, 1);
                updateSubjectMeta(subjectRepository, "COMP 023", 4, 1);
                System.out.println("✓ Subject year levels updated.");
            }

            // ── Sections + Student Assignments ────────────────────────
            if (sectionRepository.count() == 0) {
                Course bsit = courseRepository.findByCode("BSIT")
                        .orElseThrow(() -> new RuntimeException("BSIT not found"));

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

                assignSection(studentRepository, "2026-00001-SP-0", bsit1_1);
                assignSection(studentRepository, "2026-00002-SP-0", bsit1_1);
                assignSection(studentRepository, "2026-00003-SP-0", bsit1_2);
                assignSection(studentRepository, "2025-00001-SP-0", bsit2_1);
                assignSection(studentRepository, "2025-00002-SP-0", bsit2_1);
                assignSection(studentRepository, "2025-00003-SP-0", bsit2_2);
                assignSection(studentRepository, "2025-00004-SP-0", bsit2_2);
                assignSection(studentRepository, "2024-00001-SP-0", bsit3_1);
                assignSection(studentRepository, "2024-00002-SP-0", bsit3_1);
                assignSection(studentRepository, "2023-00001-SP-0", bsit4_1);

                System.out.println("✓ Sections seeded and students assigned.");
            }

            // ── Schedules ─────────────────────────────────────────────
            if (scheduleRepository.count() == 0) {

                Section bsit2_1 = sectionRepository.findAll().stream()
                        .filter(s -> s.getSectionName().equals("BSIT-SP 2-1"))
                        .findFirst().orElse(null);

                if (bsit2_1 != null) {
                    Faculty fac1 = facultyRepository.findByFacultyId("FAC-2024-001").orElse(null);
                    Faculty fac3 = facultyRepository.findByFacultyId("FAC-2024-003").orElse(null);
                    Faculty fac4 = facultyRepository.findByFacultyId("FAC-2024-004").orElse(null);
                    Faculty fac5 = facultyRepository.findByFacultyId("FAC-2024-005").orElse(null);

                    Subject comp009 = subjectRepository.findByCode("COMP 009").orElse(null);
                    Subject comp010 = subjectRepository.findByCode("COMP 010").orElse(null);
                    Subject comp013 = subjectRepository.findByCode("COMP 013").orElse(null);
                    Subject comp014 = subjectRepository.findByCode("COMP 014").orElse(null);
                    Subject elecFe2 = subjectRepository.findByCode("ELEC IT-FE2").orElse(null);
                    Subject inte202 = subjectRepository.findByCode("INTE 202").orElse(null);
                    Subject pathfit = subjectRepository.findByCode("PATHFIT 4").orElse(null);

                    // BSIT-SP 2-1 schedules
                    if (comp009 != null && fac1 != null)
                        scheduleRepository.save(buildSchedule(
                                bsit2_1, comp009, fac1,
                                "Monday", "08:00", "10:00", "Room 301",
                                CURRENT_YEAR, CURRENT_SEM));
                    if (comp009 != null && fac1 != null)
                        scheduleRepository.save(buildSchedule(
                                bsit2_1, comp009, fac1,
                                "Monday", "14:00", "17:00", "Lab 201",
                                CURRENT_YEAR, CURRENT_SEM));
                    if (comp010 != null && fac3 != null)
                        scheduleRepository.save(buildSchedule(
                                bsit2_1, comp010, fac3,
                                "Tuesday", "08:00", "10:00", "Room 302",
                                CURRENT_YEAR, CURRENT_SEM));
                    if (comp010 != null && fac3 != null)
                        scheduleRepository.save(buildSchedule(
                                bsit2_1, comp010, fac3,
                                "Tuesday", "10:30", "13:30", "Lab 202",
                                CURRENT_YEAR, CURRENT_SEM));
                    if (comp013 != null && fac5 != null)
                        scheduleRepository.save(buildSchedule(
                                bsit2_1, comp013, fac5,
                                "Wednesday", "13:00", "16:00", "Room 301",
                                CURRENT_YEAR, CURRENT_SEM));
                    if (comp014 != null && fac5 != null)
                        scheduleRepository.save(buildSchedule(
                                bsit2_1, comp014, fac5,
                                "Thursday", "17:00", "20:00", "Room 303",
                                CURRENT_YEAR, CURRENT_SEM));
                    if (elecFe2 != null && fac4 != null)
                        scheduleRepository.save(buildSchedule(
                                bsit2_1, elecFe2, fac4,
                                "Friday", "15:00", "18:00", "Room 302",
                                CURRENT_YEAR, CURRENT_SEM));
                    if (inte202 != null && fac1 != null)
                        scheduleRepository.save(buildSchedule(
                                bsit2_1, inte202, fac1,
                                "Friday", "14:00", "16:00", "Room 304",
                                CURRENT_YEAR, CURRENT_SEM));
                    if (pathfit != null && fac4 != null)
                        scheduleRepository.save(buildSchedule(
                                bsit2_1, pathfit, fac4,
                                "Saturday", "18:00", "20:00", "Gym",
                                CURRENT_YEAR, CURRENT_SEM));
                }

                System.out.println("✓ Schedules seeded.");
            }

            // ── Grades ────────────────────────────────────────────────
            if (gradeRepository.count() == 0) {

                Section bsit2_1 = sectionRepository.findAll().stream()
                        .filter(s -> s.getSectionName().equals("BSIT-SP 2-1"))
                        .findFirst().orElse(null);

                if (bsit2_1 != null) {
                    Faculty fac1 = facultyRepository.findByFacultyId("FAC-2024-001").orElse(null);
                    Faculty fac3 = facultyRepository.findByFacultyId("FAC-2024-003").orElse(null);
                    Faculty fac5 = facultyRepository.findByFacultyId("FAC-2024-005").orElse(null);

                    Subject comp009 = subjectRepository.findByCode("COMP 009").orElse(null);
                    Subject comp010 = subjectRepository.findByCode("COMP 010").orElse(null);
                    Subject comp013 = subjectRepository.findByCode("COMP 013").orElse(null);

                    // Sample grades for BSIT-SP 2-1 students
                    // School year: 2024-2025 First Semester (a completed term)
                    String prevYear = "2024-2025";
                    String prevSem  = "First Semester";

                    Student zhou = studentRepository.findByStudentNumber("2025-00001-SP-0").orElse(null);
                    Student santos = studentRepository.findByStudentNumber("2025-00002-SP-0").orElse(null);
                    Student nair = studentRepository.findByStudentNumber("2025-00003-SP-0").orElse(null);
                    Student silva = studentRepository.findByStudentNumber("2025-00004-SP-0").orElse(null);

                    if (zhou != null && comp009 != null && fac1 != null)
                        gradeRepository.save(buildGrade(zhou, comp009, bsit2_1, fac1, prevYear, prevSem, "1.5"));
                    if (zhou != null && comp010 != null && fac3 != null)
                        gradeRepository.save(buildGrade(zhou, comp010, bsit2_1, fac3, prevYear, prevSem, "1.25"));
                    if (zhou != null && comp013 != null && fac5 != null)
                        gradeRepository.save(buildGrade(zhou, comp013, bsit2_1, fac5, prevYear, prevSem, "1.75"));

                    if (santos != null && comp009 != null && fac1 != null)
                        gradeRepository.save(buildGrade(santos, comp009, bsit2_1, fac1, prevYear, prevSem, "2.0"));
                    if (santos != null && comp010 != null && fac3 != null)
                        gradeRepository.save(buildGrade(santos, comp010, bsit2_1, fac3, prevYear, prevSem, "1.75"));
                    if (santos != null && comp013 != null && fac5 != null)
                        gradeRepository.save(buildGrade(santos, comp013, bsit2_1, fac5, prevYear, prevSem, "2.25"));

                    if (nair != null && comp009 != null && fac1 != null)
                        gradeRepository.save(buildGrade(nair, comp009, bsit2_1, fac1, prevYear, prevSem, "1.0"));
                    if (nair != null && comp010 != null && fac3 != null)
                        gradeRepository.save(buildGrade(nair, comp010, bsit2_1, fac3, prevYear, prevSem, "1.25"));

                    if (silva != null && comp009 != null && fac1 != null)
                        gradeRepository.save(buildGrade(silva, comp009, bsit2_1, fac1, prevYear, prevSem, "2.5"));
                    if (silva != null && comp013 != null && fac5 != null)
                        gradeRepository.save(buildGrade(silva, comp013, bsit2_1, fac5, prevYear, prevSem, "INC"));
                }

                System.out.println("✓ Sample grades seeded.");
                System.out.println("✓ Database seeding completed.");
            }
        };
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User buildUser(String username, String rawPassword, String fullName,
            String email, Role role, PasswordEncoder encoder) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }

    private void seedFaculty(UserRepository userRepo, FacultyRepository facultyRepo,
            PasswordEncoder encoder, String facultyId, String fullName,
            String department, String status, String mobile, String email) {
        User user = buildUser(facultyId, "changeme", fullName, email, Role.FACULTY, encoder);
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

    private void seedStudent(UserRepository userRepo, StudentRepository studentRepo,
            PasswordEncoder encoder, String studentNumber, String fullName,
            Course course, Integer yearLevel, String gender, LocalDate dob) {
        User user = buildUser(studentNumber, "changeme", fullName, null, Role.STUDENT, encoder);
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

    private Subject buildSubject(String code, String name, int units,
            double lec, double lab, double tuition,
            int yearLevel, int semester, List<Course> courses) {
        Subject s = new Subject();
        s.setCode(code);
        s.setName(name);
        s.setUnits(units);
        s.setLecHours(lec);
        s.setLabHours(lab);
        s.setTuitionHours(tuition);
        s.setYearLevel(yearLevel);
        s.setSemester(semester);
        s.setCourses(new java.util.ArrayList<>(courses));
        return s;
    }

    private void updateSubjectMeta(SubjectRepository repo, String code,
            int yearLevel, int semester) {
        repo.findByCode(code).ifPresent(s -> {
            if (s.getYearLevel() == null) {
                s.setYearLevel(yearLevel);
                s.setSemester(semester);
                repo.save(s);
            }
        });
    }

    private Section buildSection(String name, Course course, int yearLevel) {
        Section s = new Section();
        s.setSectionName(name);
        s.setCourse(course);
        s.setYearLevel(yearLevel);
        return s;
    }

    private void assignSection(StudentRepository repo,
            String studentNumber, Section section) {
        repo.findByStudentNumber(studentNumber).ifPresent(s -> {
            s.setSection(section);
            repo.save(s);
        });
    }

    private Schedule buildSchedule(Section section, Subject subject,
            Faculty faculty, String day, String start, String end,
            String room, String schoolYear, String semester) {
        Schedule sc = new Schedule();
        sc.setSection(section);
        sc.setSubject(subject);
        sc.setFaculty(faculty);
        sc.setDay(day);
        sc.setStartTime(LocalTime.parse(start));
        sc.setEndTime(LocalTime.parse(end));
        sc.setRoom(room);
        sc.setSchoolYear(schoolYear);
        sc.setSemester(semester);
        return sc;
    }

    private Grade buildGrade(Student student, Subject subject,
            Section section, Faculty faculty,
            String schoolYear, String semester, String finalGrade) {
        Grade g = new Grade();
        g.setStudent(student);
        g.setSubject(subject);
        g.setSection(section);
        g.setFaculty(faculty);
        g.setSchoolYear(schoolYear);
        g.setSemester(semester);
        g.setFinalGrade(finalGrade);
        g.setGradeStatus(deriveStatus(finalGrade));
        return g;
    }

    private String deriveStatus(String finalGrade) {
        if (finalGrade == null) return null;
        return switch (finalGrade) {
            case "INC" -> "Incomplete";
            case "DRP" -> "Dropped";
            case "5.0" -> "Failed";
            default    -> "Passed";
        };
    }
}