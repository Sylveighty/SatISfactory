package com.pup.sis.controller;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.User;
import com.pup.sis.service.FacultyService;
import com.pup.sis.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faculty")
public class FacultyPortalController {

    private final FacultyService facultyService;
    private final UserService userService;

    public FacultyPortalController(
            FacultyService facultyService,
            UserService userService) {
        this.facultyService = facultyService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + auth.getName()));

        Faculty faculty = facultyService.findByUser(user).orElse(null);
        model.addAttribute("faculty", faculty);
        return "faculty/profile";
    }
}