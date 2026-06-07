package com.pup.sis.controller;

import com.pup.sis.entity.Role;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import com.pup.sis.service.CourseService;
import com.pup.sis.service.StudentService;
import com.pup.sis.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminStudentController(
            StudentService studentService,
            CourseService courseService,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ── List ─────────────────────────────────────────────────────────────────

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            Model model) {

        var students = (search != null && !search.isBlank())
                ? studentService.search(search)
                : studentService.findAll();

        model.addAttribute("students", students);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("search", search != null ? search : "");
        // Pass current year so the modal can pre-fill the year field
        model.addAttribute("currentYear", java.time.LocalDate.now().getYear());
        return "admin/students";
    }

    // ── Show edit modal ───────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found: " + id));

        model.addAttribute("students", studentService.findAll());
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("search", "");
        model.addAttribute("currentYear", java.time.LocalDate.now().getYear());
        model.addAttribute("editStudent", student);
        return "admin/students";
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @PostMapping
    public String create(
            @RequestParam Integer studentYear,
            @RequestParam Integer studentSeq,
            @RequestParam(defaultValue = "0") String studentType,
            @RequestParam String fullName,
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "1") Integer yearLevel,
            RedirectAttributes redirectAttributes) {

        // Build the formatted student number: YYYY-NNNNN-SP-0
        String studentNumber = String.format("%d-%05d-SP-%s",
                studentYear, studentSeq, studentType);

        if (studentService.findByStudentNumber(studentNumber).isPresent()) {
            redirectAttributes.addFlashAttribute("error",
                    "Student number " + studentNumber + " already exists.");
            return "redirect:/admin/students";
        }

        User user = new User();
        user.setUsername(studentNumber);
        user.setPassword(passwordEncoder.encode("changeme"));
        user.setFullName(fullName);
        user.setRole(Role.STUDENT);
        user.setEnabled(true);
        userService.save(user);

        Student student = new Student();
        student.setStudentNumber(studentNumber);
        student.setFullName(fullName);
        student.setYearLevel(yearLevel);
        student.setUser(user);
        courseService.findById(courseId).ifPresent(student::setCourse);
        studentService.save(student);

        redirectAttributes.addFlashAttribute("success",
                fullName + " added. Student number: " + studentNumber
                + ". Default password: changeme");
        return "redirect:/admin/students";
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "1") Integer yearLevel,
            RedirectAttributes redirectAttributes) {

        Student student = studentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found: " + id));

        student.setFullName(fullName);
        student.setYearLevel(yearLevel);
        courseService.findById(courseId).ifPresent(student::setCourse);
        studentService.save(student);

        // Keep the user's display name in sync
        if (student.getUser() != null) {
            student.getUser().setFullName(fullName);
            userService.save(student.getUser());
        }

        redirectAttributes.addFlashAttribute("success", "Student record updated.");
        return "redirect:/admin/students";
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Student student = studentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found: " + id));

        // Save the user ID before deleting the student row
        Long userId = student.getUser() != null ? student.getUser().getId() : null;

        studentService.delete(id);

        if (userId != null) {
            userService.delete(userId);
        }

        redirectAttributes.addFlashAttribute("success", "Student record deleted.");
        return "redirect:/admin/students";
    }
}