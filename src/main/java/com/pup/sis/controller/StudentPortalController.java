package com.pup.sis.controller;

import com.pup.sis.entity.Grade;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import com.pup.sis.service.GradeService;
import com.pup.sis.service.MessageService;
import com.pup.sis.service.ScheduleService;
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
    private final ScheduleService scheduleService;
    private final MessageService messageService;

    public StudentPortalController(
            StudentService studentService,
            UserService userService,
            SubjectService subjectService,
            GradeService gradeService,
            ScheduleService scheduleService,
            MessageService messageService) {
        this.studentService = studentService;
        this.userService = userService;
        this.subjectService = subjectService;
        this.gradeService = gradeService;
        this.scheduleService = scheduleService;
        this.messageService = messageService;
    }

    // Looks up the Student profile linked to whoever is logged in
    private Student getStudentForUser(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return studentService.findByUser(user).orElse(null);
    }

    // -- Profile ----------------------------------------------------------

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

    // -- Change Password --------------------------------------------------

    @GetMapping("/change-password")
    public String showChangePassword() {
        return "student/change-password";
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
            return "redirect:/student/change-password";
        }

        redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/student/change-password";
    }

    // -- Grades -----------------------------------------------------------

    @GetMapping("/grades")
    public String viewGrades(Authentication auth, Model model) {
        Student student = getStudentForUser(auth.getName());
        model.addAttribute("student", student);

        if (student != null) {
            List<Grade> grades = gradeService.findByStudentAndTerm(
                    student, FacultyPortalController.CURRENT_YEAR, FacultyPortalController.CURRENT_SEM);
            model.addAttribute("grades", grades);
            model.addAttribute("gpa", gradeService.calculateGPA(grades));
            model.addAttribute("schoolYear", FacultyPortalController.CURRENT_YEAR);
            model.addAttribute("semester", FacultyPortalController.CURRENT_SEM);
        } else {
            model.addAttribute("grades", List.of());
        }
        return "student/grades";
    }

    // -- Schedule ---------------------------------------------------------

    @GetMapping("/schedule")
    public String viewSchedule(Authentication auth, Model model) {
        Student student = getStudentForUser(auth.getName());
        model.addAttribute("student", student);

        if (student != null && student.getSection() != null) {
            // Get subjects the student is enrolled in this term
            List<Grade> enrolledGrades = gradeService.findByStudentAndTerm(
                    student,
                    FacultyPortalController.CURRENT_YEAR,
                    FacultyPortalController.CURRENT_SEM);

            // Get all schedules for the section, then filter to only enrolled subjects
            List<com.pup.sis.entity.Schedule> fullSchedule = scheduleService.findBySectionAndTerm(
                    student.getSection(),
                    FacultyPortalController.CURRENT_YEAR,
                    FacultyPortalController.CURRENT_SEM);

            var enrolledSubjectIds = enrolledGrades.stream()
                    .map(g -> g.getSubject().getId())
                    .toList();

            var filteredSchedule = fullSchedule.stream()
                    .filter(sc -> enrolledSubjectIds.contains(sc.getSubject().getId()))
                    .toList();

            model.addAttribute("schedule", filteredSchedule);
        } else {
            model.addAttribute("schedule", List.of());
        }

        model.addAttribute("schoolYear", FacultyPortalController.CURRENT_YEAR);
        model.addAttribute("semester", FacultyPortalController.CURRENT_SEM);
        return "student/schedule";
    }

    // -- Enrollment -------------------------------------------------------

    @GetMapping("/enrollment")
    public String viewEnrollment(Authentication auth, Model model) {
        Student student = getStudentForUser(auth.getName());
        model.addAttribute("student", student);

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

        if (subjectIds == null || subjectIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one subject.");
            return "redirect:/student/enrollment";
        }

        if (student.getSection() == null) {
            redirectAttributes.addFlashAttribute("error",
                    "You have not been assigned to a section yet. Please contact the administrator.");
            return "redirect:/student/enrollment";
        }

        // Create a Grade placeholder for each selected subject (no grade yet)
        for (Long subjectId : subjectIds) {
            subjectService.findById(subjectId).ifPresent(subject -> {
                // Skip if already enrolled in this subject this term
                boolean alreadyEnrolled = gradeService.findByStudentSubjectAndTerm(
                        student, subject,
                        FacultyPortalController.CURRENT_YEAR,
                        FacultyPortalController.CURRENT_SEM).isPresent();

                if (!alreadyEnrolled) {
                    Grade grade = new Grade();
                    grade.setStudent(student);
                    grade.setSubject(subject);
                    grade.setSection(student.getSection());
                    grade.setSchoolYear(FacultyPortalController.CURRENT_YEAR);
                    grade.setSemester(FacultyPortalController.CURRENT_SEM);
                    gradeService.save(grade);
                }
            });
        }

        redirectAttributes.addFlashAttribute("success", "Enrollment saved successfully.");
        return "redirect:/student/enrollment/confirm";
    }

    @GetMapping("/enrollment/confirm")
    public String confirmEnrollment(Authentication auth, Model model) {
        Student student = getStudentForUser(auth.getName());
        model.addAttribute("student", student);

        if (student != null) {
            List<Grade> enrolled = gradeService.findByStudentAndTerm(
                    student,
                    FacultyPortalController.CURRENT_YEAR,
                    FacultyPortalController.CURRENT_SEM);
            model.addAttribute("enrolledGrades", enrolled);

            int totalUnits = enrolled.stream()
                    .mapToInt(g -> g.getSubject().getUnits() != null ? g.getSubject().getUnits() : 0)
                    .sum();
            model.addAttribute("totalUnits", totalUnits);
        }

        model.addAttribute("schoolYear", FacultyPortalController.CURRENT_YEAR);
        model.addAttribute("semester", FacultyPortalController.CURRENT_SEM);
        return "student/enrollment-confirm";
    }

    // -- Inbox ------------------------------------------------------------

    @GetMapping("/inbox")
    public String inbox(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("messages", messageService.findByRecipient(user));
        model.addAttribute("unreadCount", messageService.countUnread(user));
        return "student/inbox";
    }

    @PostMapping("/inbox/{id}/read")
    public String markRead(@PathVariable Long id, Authentication auth) {
        messageService.findById(id).ifPresent(m -> {
            if (m.getRecipient().getUsername().equals(auth.getName())) {
                messageService.markAsRead(id);
            }
        });
        return "redirect:/student/inbox";
    }
}