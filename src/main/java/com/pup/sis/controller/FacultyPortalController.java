package com.pup.sis.controller;

import com.pup.sis.entity.*;
import com.pup.sis.service.*;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/faculty")
public class FacultyPortalController {

    static final String CURRENT_YEAR = "2025-2026";
    static final String CURRENT_SEM  = "Second Semester";

    private final FacultyService facultyService;
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final StudentService studentService;
    private final SectionService sectionService;
    private final SubjectService subjectService;
    private final GradeService gradeService;

    public FacultyPortalController(
            FacultyService facultyService,
            UserService userService,
            ScheduleService scheduleService,
            StudentService studentService,
            SectionService sectionService,
            SubjectService subjectService,
            GradeService gradeService) {
        this.facultyService = facultyService;
        this.userService = userService;
        this.scheduleService = scheduleService;
        this.studentService = studentService;
        this.sectionService = sectionService;
        this.subjectService = subjectService;
        this.gradeService = gradeService;
    }

    private Faculty getFacultyForUser(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return facultyService.findByUser(user).orElse(null);
    }

    // -- Profile ----------------------------------------------------------

    @GetMapping("/profile")
    public String viewProfile(Authentication auth, Model model) {
        model.addAttribute("faculty", getFacultyForUser(auth.getName()));
        return "faculty/profile";
    }

    // -- Change Password --------------------------------------------------

    @GetMapping("/change-password")
    public String showChangePassword() {
        return "faculty/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            Authentication auth,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + auth.getName()));

        String error = userService.changePassword(user, currentPassword, newPassword, confirmPassword);

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/faculty/change-password";
        }

        redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/faculty/change-password";
    }

    // -- Schedules --------------------------------------------------------

    @GetMapping("/schedules")
    public String viewSchedule(Authentication auth, Model model) {
        Faculty faculty = getFacultyForUser(auth.getName());
        model.addAttribute("faculty", faculty);

        if (faculty != null) {
            model.addAttribute("schedules",
                    scheduleService.findByFacultyAndTerm(faculty, CURRENT_YEAR, CURRENT_SEM));
        } else {
            model.addAttribute("schedules", List.of());
        }
        return "faculty/schedules";
    }

    // -- Classes ----------------------------------------------------------

    @GetMapping("/classes")
    public String viewClasses(Authentication auth, Model model) {
        Faculty faculty = getFacultyForUser(auth.getName());
        model.addAttribute("faculty", faculty);

        List<Schedule> schedules = faculty != null
                ? scheduleService.findByFacultyAndTerm(faculty, CURRENT_YEAR, CURRENT_SEM)
                : List.of();

        model.addAttribute("schedules", schedules);
        return "faculty/classes";
    }

    @GetMapping("/classes/{sectionId}")
    public String classDetail(
            Authentication auth,
            @PathVariable Long sectionId,
            Model model) {

        Faculty faculty = getFacultyForUser(auth.getName());
        Schedule matched = scheduleService.findAll().stream()
                .filter(s -> s.getSection().getId().equals(sectionId))
                .filter(s -> faculty != null && s.getFaculty().getId().equals(faculty.getId()))
                .findFirst().orElse(null);

        if (matched == null) {
            model.addAttribute("section", null);
            model.addAttribute("students", List.of());
            return "faculty/class-detail";
        }

        Section section = matched.getSection();
        model.addAttribute("section", section);
        model.addAttribute("subject", matched.getSubject());
        model.addAttribute("students", studentService.findBySection(section));
        return "faculty/class-detail";
    }

    // -- Grades -----------------------------------------------------------

    @GetMapping("/grades")
    public String gradeForm(
            Authentication auth,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long sectionId,
            Model model) {

        Faculty faculty = getFacultyForUser(auth.getName());
        model.addAttribute("faculty", faculty);

        List<Schedule> schedules = faculty != null
                ? scheduleService.findByFacultyAndTerm(faculty, CURRENT_YEAR, CURRENT_SEM)
                : List.of();

        model.addAttribute("subjects", schedules.stream()
                .map(Schedule::getSubject).distinct().toList());

        model.addAttribute("selectedSubjectId", subjectId);
        model.addAttribute("selectedSectionId", sectionId);

        if (subjectId != null && sectionId != null) {
            Schedule matched = schedules.stream()
                    .filter(s -> s.getSubject().getId().equals(subjectId))
                    .filter(s -> s.getSection().getId().equals(sectionId))
                    .findFirst().orElse(null);

            if (matched != null) {
                Section section = matched.getSection();
                Subject subject = matched.getSubject();
                List<Student> students = studentService.findBySection(section);

                model.addAttribute("section", section);
                model.addAttribute("subject", subject);
                model.addAttribute("students", students);

                List<Grade> existing = gradeService.findBySectionSubjectAndTerm(
                        section, subject, CURRENT_YEAR, CURRENT_SEM);
                model.addAttribute("existingGrades", existing.stream()
                        .collect(Collectors.toMap(
                                g -> g.getStudent().getId(), g -> g)));
            }
        }

        return "faculty/grades";
    }

    @PostMapping("/grades")
    public String submitGrades(
            Authentication auth,
            @RequestParam Long sectionId,
            @RequestParam Long subjectId,
            @RequestParam List<Long> studentIds,
            @RequestParam Map<Long, String> grades,
            RedirectAttributes redirectAttributes) {

        Faculty faculty = getFacultyForUser(auth.getName());
        Section section = sectionService.findById(sectionId).orElse(null);
        Subject subject = subjectService.findById(subjectId).orElse(null);

        if (faculty != null && section != null && subject != null) {
            for (Long studentId : studentIds) {
                Student student = studentService.findById(studentId).orElse(null);
                String finalGrade = grades.get(studentId);
                if (student == null || finalGrade == null || finalGrade.isBlank()) continue;

                Grade grade = gradeService.findByStudentSubjectAndTerm(
                        student, subject, CURRENT_YEAR, CURRENT_SEM).orElse(new Grade());

                grade.setStudent(student);
                grade.setSubject(subject);
                grade.setSection(section);
                grade.setFaculty(faculty);
                grade.setSchoolYear(CURRENT_YEAR);
                grade.setSemester(CURRENT_SEM);
                grade.setFinalGrade(finalGrade);
                grade.setGradeStatus(gradeService.deriveStatus(finalGrade));
                gradeService.save(grade);
            }
            redirectAttributes.addFlashAttribute("success", "Grades submitted.");
        }

        return "redirect:/faculty/grades?subjectId=" + subjectId + "&sectionId=" + sectionId;
    }
}