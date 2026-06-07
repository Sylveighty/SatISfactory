package com.pup.sis.controller;

import com.pup.sis.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "courses/list";
    }
}