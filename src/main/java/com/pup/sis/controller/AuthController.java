package com.pup.sis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/")
    public String root() {
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcomePage() {
        return "welcome";
    }

    @GetMapping("/login/student")
    public String studentLogin(
            @RequestParam(required = false) String error,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid student number or password.");
        }
        model.addAttribute("forgotPasswordUrl", "/forgot-password");
        return "login-student";
    }

    @GetMapping("/login/faculty")
    public String facultyLogin(
            @RequestParam(required = false) String error,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid faculty ID or password.");
        }
        model.addAttribute("forgotPasswordUrl", "/forgot-password");
        return "login-faculty";
    }

    @GetMapping("/login/admin")
    public String adminLogin(
            @RequestParam(required = false) String error,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid credentials. Please try again.");
        }
        model.addAttribute("forgotPasswordUrl", "/forgot-password");
        return "login-admin";
    }
}