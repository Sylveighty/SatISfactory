package com.pup.sis.controller;

import com.pup.sis.service.FacultyChangePasswordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/faculty")
public class FacultyChangePasswordController {

    private final FacultyChangePasswordService facultyChangePasswordService;

    public FacultyChangePasswordController(FacultyChangePasswordService facultyChangePasswordService) {
        this.facultyChangePasswordService = facultyChangePasswordService;
    }

    @GetMapping("/Change-Password-Faculty")
    public String showChangePasswordPage() {
        return "faculty/Change-Password-Faculty";
    }

    @PostMapping("/Change-Password-Faculty")
    public String changePassword(
            @RequestParam String facultyId,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        String result = facultyChangePasswordService.changePassword(
                facultyId,
                currentPassword,
                newPassword,
                confirmPassword
        );

        if ("SUCCESS".equals(result)) {
            model.addAttribute("success", "Password changed successfully.");
        } else {
            model.addAttribute("error", result);
        }

        return "faculty/Change-Password-Faculty";
    }
}