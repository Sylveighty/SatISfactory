package com.pup.sis.controller;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Subject;
import com.pup.sis.service.CourseService;
import com.pup.sis.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/curriculum")
public class AdminCurriculumController {

    private final CourseService courseService;
    private final SubjectService subjectService;

    public AdminCurriculumController(
            CourseService courseService,
            SubjectService subjectService) {
        this.courseService = courseService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public String curriculum(Model model) {
        // LinkedHashMap preserves insertion order of courses
        Map<Course, List<Subject>> curriculumMap = new LinkedHashMap<>();
        for (Course course : courseService.findAll()) {
            curriculumMap.put(course, subjectService.findByCourse(course));
        }
        model.addAttribute("curriculumMap", curriculumMap);
        return "admin/curriculum";
    }
}