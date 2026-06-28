package com.pup.sis.controller;

import com.pup.sis.entity.Role;
import com.pup.sis.entity.User;
import com.pup.sis.service.MessageService;
import com.pup.sis.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/messages")
public class AdminMessageController {

    private final MessageService messageService;
    private final UserService userService;

    public AdminMessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping
    public String compose(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/messages";
    }

    @PostMapping("/send")
    public String send(
            Authentication auth,
            @RequestParam String recipientType,
            @RequestParam(required = false) Long recipientId,
            @RequestParam String subject,
            @RequestParam String body,
            RedirectAttributes redirectAttributes) {

        User sender = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        switch (recipientType) {
            case "student" -> messageService.broadcast(sender, Role.STUDENT, subject, body);
            case "faculty" -> messageService.broadcast(sender, Role.FACULTY, subject, body);
            case "specific" -> {
                if (recipientId == null) {
                    redirectAttributes.addFlashAttribute("error", "Please select a recipient.");
                    return "redirect:/admin/messages";
                }
                userService.findById(recipientId).ifPresent(recipient ->
                        messageService.send(sender, recipient, subject, body));
            }
            default -> {
                redirectAttributes.addFlashAttribute("error", "Invalid recipient type.");
                return "redirect:/admin/messages";
            }
        }

        redirectAttributes.addFlashAttribute("success", "Message sent successfully.");
        return "redirect:/admin/messages";
    }
}