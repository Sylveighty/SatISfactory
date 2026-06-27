package com.pup.sis.controller;

import com.pup.sis.entity.PasswordResetToken;
import com.pup.sis.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // -- Forgot Password (request form) -----------------------------------

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        try {
            passwordResetService.requestReset(email);
        } catch (Exception e) {
            // Mail failure or other error - still show generic message
            // so we don't leak whether the email exists
        }

        // Always show the same message regardless of outcome
        redirectAttributes.addFlashAttribute("message",
                "If that email is registered in the system, a reset link has been sent. " +
                "Please check your inbox.");
        return "redirect:/forgot-password";
    }

    // -- Reset Password (token form) --------------------------------------

    @GetMapping("/reset-password")
    public String showResetPassword(
            @RequestParam String token,
            Model model) {

        Optional<PasswordResetToken> valid = passwordResetService.validateToken(token);

        if (valid.isEmpty()) {
            model.addAttribute("error",
                    "This reset link is invalid or has expired. Please request a new one.");
            model.addAttribute("tokenInvalid", true);
        } else {
            model.addAttribute("token", token);
        }

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        String error = passwordResetService.resetPassword(token, newPassword, confirmPassword);

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/reset-password?token=" + token;
        }

        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/reset-password-success";
    }

    // -- Success page -----------------------------------------------------

    @GetMapping("/reset-password-success")
    public String resetSuccess() {
        return "reset-password-success";
    }
}