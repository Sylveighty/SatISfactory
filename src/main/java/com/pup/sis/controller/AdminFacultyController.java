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

    // ── List ──────────────────────────────────────────────────────────────────

    @GetMapping
    public String list(Model model) {
        model.addAttribute("facultyList", facultyService.findAll());
        return "admin/faculty";
    }

    // ── Show edit modal ───────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Faculty faculty = facultyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found: " + id));

        model.addAttribute("facultyList", facultyService.findAll());
        model.addAttribute("editFaculty", faculty);
        return "admin/faculty";
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @PostMapping
    public String create(
            @RequestParam String facultyId,
            @RequestParam String fullName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false, defaultValue = "Full Time") String status,
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) String email,
            RedirectAttributes redirectAttributes) {

        // Check for duplicate faculty ID
        if (facultyService.findByFacultyId(facultyId).isPresent()) {
            redirectAttributes.addFlashAttribute("error",
                    "Faculty ID " + facultyId + " already exists.");
            return "redirect:/admin/faculty";
        }

        // Create login account - faculty ID is the username
        User user = new User();
        user.setUsername(facultyId);
        user.setPassword(passwordEncoder.encode("changeme"));
        user.setFullName(fullName);
        user.setRole(Role.FACULTY);
        user.setEnabled(true);
        userService.save(user);

        // Create faculty profile
        Faculty faculty = new Faculty();
        faculty.setFacultyId(facultyId);
        faculty.setFullName(fullName);
        faculty.setDepartment(department);
        faculty.setStatus(status);
        faculty.setMobileNumber(mobileNumber);
        faculty.setEmail(email);
        faculty.setUser(user);
        facultyService.save(faculty);

        redirectAttributes.addFlashAttribute("success",
                fullName + " added. Default password: changeme");
        return "redirect:/admin/faculty";
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false, defaultValue = "Full Time") String status,
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) String email,
            RedirectAttributes redirectAttributes) {

        Faculty faculty = facultyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found: " + id));

        faculty.setFullName(fullName);
        faculty.setDepartment(department);
        faculty.setStatus(status);
        faculty.setMobileNumber(mobileNumber);
        faculty.setEmail(email);
        facultyService.save(faculty);

        if (faculty.getUser() != null) {
            faculty.getUser().setFullName(fullName);
            userService.save(faculty.getUser());
        }

        redirectAttributes.addFlashAttribute("success", "Faculty record updated.");
        return "redirect:/admin/faculty";
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Faculty faculty = facultyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Faculty not found: " + id));

        Long userId = faculty.getUser() != null ? faculty.getUser().getId() : null;

        facultyService.delete(id);

        if (userId != null) {
            userService.delete(userId);
        }

        redirectAttributes.addFlashAttribute("success", "Faculty record deleted.");
        return "redirect:/admin/faculty";
    }
}