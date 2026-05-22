package com.pup.sis.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "admin/dashboard";
    }

    @GetMapping("/faculty/dashboard")
    public String facultyDashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "faculty/dashboard";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "student/dashboard";
    }
}