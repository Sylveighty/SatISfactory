package com.pup.sis.controller;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Role;
import com.pup.sis.entity.User;
import com.pup.sis.service.FacultyService;
import com.pup.sis.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/faculty")
public class AdminFacultyController {

    private final FacultyService facultyService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminFacultyController(
            FacultyService facultyService,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.facultyService = facultyService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Retrieve and display all active faculty members
    @GetMapping
    public String list(Model model) {
        // Add all active faculty members to the model for display
        model.addAttribute("facultyList", facultyService.findAll());
        return "admin/faculty";
    }

    // Display the edit form for a specific faculty record
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        // Retrieve the faculty by ID, throwing an exception if not found
        Faculty faculty = facultyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found: " + id));

        // Populate the model with all necessary data for editing
        model.addAttribute("facultyList", facultyService.findAll());
        // Mark this faculty member as the one being edited in the UI
        model.addAttribute("editFaculty", faculty);
        return "admin/faculty";
    }

    // Create a new faculty record with associated user account
    @PostMapping
    public String create(
            @RequestParam String facultyId,
            @RequestParam String fullName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false, defaultValue = "Full Time") String status,
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) String email,
            RedirectAttributes redirectAttributes) {

        // Check if the faculty ID is already held by an ACTIVE faculty member.
        // A deactivated record (e.g. a corrected mis-encode) does not block reuse.
        if (facultyService.isFacultyIdActive(facultyId)) {
            redirectAttributes.addFlashAttribute("error",
                    "Faculty ID " + facultyId + " already exists.");
            return "redirect:/admin/faculty";
        }

        // Create a new user account for the faculty member
        // The faculty ID is used as the login username
        User user = new User();
        user.setUsername(facultyId);
        user.setPassword(passwordEncoder.encode("changeme")); // Default password that must be changed
        user.setFullName(fullName);
        user.setRole(Role.FACULTY);
        user.setEnabled(true);
        userService.save(user);

        // Create the faculty profile linked to the user account
        Faculty faculty = new Faculty();
        faculty.setFacultyId(facultyId);
        faculty.setFullName(fullName);
        faculty.setDepartment(department);
        faculty.setStatus(status);
        faculty.setMobileNumber(mobileNumber);
        faculty.setEmail(email);
        faculty.setUser(user);
        facultyService.save(faculty);

        // Notify admin of successful creation with faculty details
        redirectAttributes.addFlashAttribute("success",
                fullName + " added. Default password: changeme");
        return "redirect:/admin/faculty";
    }

    // Update an existing faculty record and associated user account
    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false, defaultValue = "Full Time") String status,
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) String email,
            RedirectAttributes redirectAttributes) {

        // Retrieve the faculty by ID, throwing an exception if not found
        Faculty faculty = facultyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found: " + id));

        // Update the faculty's profile information
        faculty.setFullName(fullName);
        faculty.setDepartment(department);
        faculty.setStatus(status);
        faculty.setMobileNumber(mobileNumber);
        faculty.setEmail(email);
        facultyService.save(faculty);

        // Keep the user account's display name synchronized with the faculty record
        if (faculty.getUser() != null) {
            faculty.getUser().setFullName(fullName);
            userService.save(faculty.getUser());
        }

        // Notify admin of successful update
        redirectAttributes.addFlashAttribute("success", "Faculty record updated.");
        return "redirect:/admin/faculty";
    }

    // Soft delete: deactivates the faculty's login account while preserving all records.
    // Their schedule assignments and grade records remain intact in the database.
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        // Retrieve the faculty by ID, throwing an exception if not found
        Faculty faculty = facultyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found: " + id));

        String name = faculty.getFullName();

        // Disable the faculty's login account — records are preserved
        facultyService.deactivate(id);

        redirectAttributes.addFlashAttribute("success",
                name + "'s account has been deactivated. Their records are preserved.");
        return "redirect:/admin/faculty";
    }
}