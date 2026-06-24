package com.pup.sis.controller;

import com.pup.sis.entity.Grade;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import com.pup.sis.service.GradeService;
import com.pup.sis.service.StudentService;
import com.pup.sis.service.SubjectService;
import com.pup.sis.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    private final StudentService studentService;
    private final UserService userService;
    private final SubjectService subjectService;
    private final GradeService gradeService;

    public StudentPortalController(
            StudentService studentService,
            UserService userService,
            SubjectService subjectService,
            GradeService gradeService) {
        this.studentService = studentService;
        this.userService = userService;
        this.subjectService = subjectService;
        this.gradeService = gradeService;
    }

    // Looks up the Student profile linked to whoever is logged in
    private Student getStudentForUser(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return studentService.findByUser(user).orElse(null);
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    @GetMapping("/profile")
    public String viewProfile(Authentication auth, Model model) {
        model.addAttribute("student", getStudentForUser(auth.getName()));
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            Authentication auth,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String placeOfBirth,
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String residentialAddress,
            @RequestParam(required = false) String permanentAddress,
            @RequestParam(required = false) String spouseName,
            RedirectAttributes redirectAttributes) {

        Student student = getStudentForUser(auth.getName());

        if (student == null) {
            redirectAttributes.addFlashAttribute("error",
                    "No student profile found for this account.");
            return "redirect:/student/profile";
        }

        student.setGender(gender);

        if (dateOfBirth != null && !dateOfBirth.isBlank()) {
            student.setDateOfBirth(LocalDate.parse(dateOfBirth));
        } else {
            student.setDateOfBirth(null);
        }

        student.setPlaceOfBirth(placeOfBirth);
        student.setMobileNumber(mobileNumber);
        student.setEmail(email);
        student.setResidentialAddress(residentialAddress);
        student.setPermanentAddress(permanentAddress);
        student.setSpouseName(spouseName);
        studentService.save(student);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/student/profile";
    }

    // ── Grades ────────────────────────────────────────────────────────────────

    @GetMapping("/grades")
    public String viewGrades(Authentication auth, Model model) {
        Student student = getStudentForUser(auth.getName());
        model.addAttribute("student", student);

        if (student != null) {
            List<Grade> grades = gradeService.findByStudentAndTerm(
                    student, "2024-2025", "First Semester");
            model.addAttribute("grades", grades);
            model.addAttribute("gpa", gradeService.calculateGPA(grades));
            model.addAttribute("schoolYear", "2024-2025");
            model.addAttribute("semester", "First Semester");
        } else {
            model.addAttribute("grades", List.of());
        }
        return "student/grades";
    }

    // ── Enrollment ────────────────────────────────────────────────────────────

    @GetMapping("/enrollment")
    public String viewEnrollment(Authentication auth, Model model) {
        Student student = getStudentForUser(auth.getName());
        model.addAttribute("student", student);

        // Subjects available to the student's course.
        // NOTE: Subject has no yearLevel field yet, so this currently shows
        // all subjects linked to the student's course regardless of year.
        // Proper year-level curriculum filtering is a future enhancement.
        if (student != null && student.getCourse() != null) {
            model.addAttribute("subjects", subjectService.findByCourse(student.getCourse()));
        } else {
            model.addAttribute("subjects", List.of());
        }

        return "student/enrollment";
    }

    @PostMapping("/enrollment")
    public String saveEnrollment(
            Authentication auth,
            @RequestParam(required = false) List<Long> subjectIds,
            RedirectAttributes redirectAttributes) {

        Student student = getStudentForUser(auth.getName());
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "No student profile found.");
            return "redirect:/student/enrollment";
        }

        // For Step 4, enrollment selection is acknowledged but not yet
        // persisted to a dedicated Enrollment entity - that comes in Step 5
        // alongside the assessment and confirmation slip generation.
        redirectAttributes.addFlashAttribute("enrolledSubjectIds", subjectIds);
        return "redirect:/student/enrollment/confirm";
    }

    @GetMapping("/enrollment/confirm")
    public String confirmEnrollment(Authentication auth, Model model) {
        model.addAttribute("student", getStudentForUser(auth.getName()));
        return "student/enrollment-confirm";
    }
}