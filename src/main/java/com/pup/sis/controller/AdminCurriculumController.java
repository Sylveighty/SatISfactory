package com.pup.sis.controller;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Subject;
import com.pup.sis.service.CourseService;
import com.pup.sis.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        Map<Course, List<Subject>> curriculumMap = new LinkedHashMap<>();

        for (Course course : courseService.findAll()) {
            curriculumMap.put(course, subjectService.findByCourse(course));
        }

        model.addAttribute("curriculumMap", curriculumMap);

        return "admin/curriculum";
    }

    // --------------------------------------------------
    // Create Course
    // --------------------------------------------------

    @PostMapping
public String createCourse(
        @ModelAttribute Course course,
        RedirectAttributes redirectAttributes) {

    course.setCode(course.getCode().trim().toUpperCase());

    if (courseService.courseCodeExists(course.getCode())) {

        redirectAttributes.addFlashAttribute(
                "error",
                "Course code '" + course.getCode() + "' already exists.");

        return "redirect:/admin/curriculum";
    }

    courseService.save(course);

    redirectAttributes.addFlashAttribute(
            "success",
            "Course added successfully.");

    return "redirect:/admin/curriculum";
}
    

    // --------------------------------------------------
    // Edit Course
    // --------------------------------------------------

    @GetMapping("/{id}/edit")
    public String editCourse(
            @PathVariable Long id,
            Model model) {

        Map<Course, List<Subject>> curriculumMap = new LinkedHashMap<>();

        for (Course course : courseService.findAll()) {
            curriculumMap.put(course, subjectService.findByCourse(course));
        }

        Course course = courseService.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        model.addAttribute("curriculumMap", curriculumMap);
        model.addAttribute("editCourse", course);

        return "admin/curriculum";
    }

    // --------------------------------------------------
    // Update Course
    // --------------------------------------------------

    @PostMapping("/{id}")
public String updateCourse(
        @PathVariable Long id,
        @RequestParam String code,
        @RequestParam String name,
        RedirectAttributes redirectAttributes) {

    code = code.trim().toUpperCase();

    if (courseService.courseCodeExistsForAnotherCourse(id, code)) {

        redirectAttributes.addFlashAttribute(
                "error",
                "Course code '" + code + "' already exists.");

        return "redirect:/admin/curriculum";
    }

    Course course = courseService.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Course not found"));

    course.setCode(code);
    course.setName(name);

    courseService.save(course);

    redirectAttributes.addFlashAttribute(
            "success",
            "Course updated successfully.");

    return "redirect:/admin/curriculum";
}

    // --------------------------------------------------
    // Delete Course
    // --------------------------------------------------

    @PostMapping("/{id}/delete")
    public String deleteCourse(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            courseService.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Course deleted successfully.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to delete this course because it is currently being used.");

        }

        return "redirect:/admin/curriculum";
    }

}