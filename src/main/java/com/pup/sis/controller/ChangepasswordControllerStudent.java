package com.pup.sis.controller;

import com.pup.sis.service.ChangePasswordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class ChangepasswordControllerStudent {

    private final ChangePasswordService changePasswordService;

    public ChangepasswordControllerStudent(ChangePasswordService changePasswordService) {
        this.changePasswordService = changePasswordService;
    }

    @GetMapping("/Change-password")
    public String showChangePasswordPage() {
        return "student/Change-password";
    }

    @PostMapping("/Change-password")
    public String changePassword(
            @RequestParam String studentNumber,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        String result = changePasswordService.changePassword(
                studentNumber,
                currentPassword,
                newPassword,
                confirmPassword
        );

        if ("SUCCESS".equals(result)) {
            model.addAttribute("success",
                    "Password changed successfully.");
        } else {
            model.addAttribute("error", result);
        }

        return "student/Change-password";
    }
}