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

    // Retrieve and display all students with optional search filtering
    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            Model model) {

        // Search students if a search term is provided, otherwise retrieve all students
        var students = (search != null && !search.isBlank())
                ? studentService.search(search)
                : studentService.findAll();

        // Add students and available courses to the model for display
        model.addAttribute("students", students);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("search", search != null ? search : "");
        // Pre-fill the year field in the creation modal with the current year
        model.addAttribute("currentYear", java.time.LocalDate.now().getYear());
        return "admin/students";
    }

    // Display the edit form for a specific student record
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        // Retrieve the student by ID, throwing an exception if not found
        Student student = studentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found: " + id));

        // Populate the model with all necessary data for editing
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("search", "");
        model.addAttribute("currentYear", java.time.LocalDate.now().getYear());
        // Mark this student as the one being edited in the UI
        model.addAttribute("editStudent", student);
        return "admin/students";
    }

    // Create a new student record with associated user account
    @PostMapping
    public String create(
            @RequestParam Integer studentYear,
            @RequestParam Integer studentSeq,
            @RequestParam(defaultValue = "0") String studentType,
            @RequestParam String fullName,
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "1") Integer yearLevel,
            RedirectAttributes redirectAttributes) {

        // Generate a unique student number in the format: YYYY-NNNNN-SP-TYPE
        String studentNumber = String.format("%d-%05d-SP-%s",
                studentYear, studentSeq, studentType);

        // Check if the student number already exists to prevent duplicates
        if (studentService.findByStudentNumber(studentNumber).isPresent()) {
            redirectAttributes.addFlashAttribute("error",
                    "Student number " + studentNumber + " already exists.");
            return "redirect:/admin/students";
        }

        // Create a new user account for the student
        User user = new User();
        user.setUsername(studentNumber);
        user.setPassword(passwordEncoder.encode("changeme")); // Default password that must be changed
        user.setFullName(fullName);
        user.setRole(Role.STUDENT);
        user.setEnabled(true);
        userService.save(user);

        // Create the student record linked to the user account
        Student student = new Student();
        student.setStudentNumber(studentNumber);
        student.setFullName(fullName);
        student.setYearLevel(yearLevel);
        student.setUser(user);
        // Assign the selected course to the student
        courseService.findById(courseId).ifPresent(student::setCourse);
        studentService.save(student);

        // Notify admin of successful creation with student details
        redirectAttributes.addFlashAttribute("success",
                fullName + " added. Student number: " + studentNumber
                + ". Default password: changeme");
        return "redirect:/admin/students";
    }

    // Update an existing student record and associated user account
    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "1") Integer yearLevel,
            RedirectAttributes redirectAttributes) {

        // Retrieve the student by ID, throwing an exception if not found
        Student student = studentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found: " + id));

        // Update the student's profile information
        student.setFullName(fullName);
        student.setYearLevel(yearLevel);
        // Update the course assignment if a new course is selected
        courseService.findById(courseId).ifPresent(student::setCourse);
        studentService.save(student);

        // Keep the user account's display name synchronized with the student record
        if (student.getUser() != null) {
            student.getUser().setFullName(fullName);
            userService.save(student.getUser());
        }

        // Notify admin of successful update
        redirectAttributes.addFlashAttribute("success", "Student record updated.");
        return "redirect:/admin/students";
    }

    // Delete a student record and their associated user account
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        // Retrieve the student by ID, throwing an exception if not found
        Student student = studentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found: " + id));

        // Extract the user ID before deleting the student record
        // This is necessary because deleting the student will break the foreign key relationship
        Long userId = student.getUser() != null ? student.getUser().getId() : null;

        // Delete the student record from the database
        studentService.delete(id);

        // Delete the associated user account to maintain data consistency
        if (userId != null) {
            userService.delete(userId);
        }

        // Notify admin of successful deletion
        redirectAttributes.addFlashAttribute("success", "Student record deleted.");
        return "redirect:/admin/students";
    }
}