package com.pup.sis.controller;

import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import com.pup.sis.service.StudentService;
import com.pup.sis.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    private final StudentService studentService;
    private final UserService userService;

    public StudentPortalController(
            StudentService studentService,
            UserService userService) {
        this.studentService = studentService;
        this.userService = userService;
    }

    // Helper method to retrieve a student record for the currently logged-in user
    // Returns null if no student profile is found for the user
    private Student getStudentForUser(String username) {
        // Find the user account by username
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        // Find the student profile associated with this user
        return studentService.findByUser(user).orElse(null);
    }

    // Display and update the student's profile information──

    // Display the student's profile information
    @GetMapping("/profile")
    public String viewProfile(Authentication auth, Model model) {
        // Add the logged-in student's profile to the model
        model.addAttribute("student", getStudentForUser(auth.getName()));
        return "student/profile";
    }

    // Update the student's profile information
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

        // Retrieve the logged-in student's profile
        Student student = getStudentForUser(auth.getName());

        if (student == null) {
            redirectAttributes.addFlashAttribute("error",
                    "No student profile found for this account.");
            return "redirect:/student/profile";
        }

        // Update the student's demographic information
        student.setGender(gender);

        // Parse the date of birth from the HTML date input (format: yyyy-MM-dd)
        if (dateOfBirth != null && !dateOfBirth.isBlank()) {
            student.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
        } else {
            student.setDateOfBirth(null);
        }

        // Update contact and address information
        student.setPlaceOfBirth(placeOfBirth);
        student.setMobileNumber(mobileNumber);
        student.setEmail(email);
        student.setResidentialAddress(residentialAddress);
        student.setPermanentAddress(permanentAddress);
        student.setSpouseName(spouseName);
        studentService.save(student);

        // Notify student of successful update
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/student/profile";
    }

    // Display the student's grades and academic performance
    @GetMapping("/grades")
    public String viewGrades(Authentication auth, Model model) {
        // Add the logged-in student's profile to the model
        model.addAttribute("student", getStudentForUser(auth.getName()));
        // Grade data will be populated and displayed from the student record
        return "student/grades";
    }
}