package com.pup.sis.controller;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Subject;
import com.pup.sis.service.CourseService;
import com.pup.sis.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/subjects")
public class AdminSubjectController {

    private final SubjectService subjectService;
    private final CourseService courseService;

    public AdminSubjectController(
            SubjectService subjectService,
            CourseService courseService) {
        this.subjectService = subjectService;
        this.courseService = courseService;
    }

    // Retrieve and display all subjects with optional search filtering
    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            Model model) {

        // Search subjects if a search term is provided, otherwise retrieve all subjects
        var subjects = (search != null && !search.isBlank())
                ? subjectService.search(search)
                : subjectService.findAll();

        // Add subjects and available courses to the model for display
        model.addAttribute("subjects", subjects);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("search", search != null ? search : "");
        return "admin/subjects";
    }

    // Display the edit form for a specific subject record
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        // Retrieve the subject by ID, throwing an exception if not found
        Subject subject = subjectService.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));

        // Extract selected course IDs as a Set for easy checkbox comparison in the template
        Set<Long> selectedCourseIds = subject.getCourses().stream()
                .map(Course::getId)
                .collect(Collectors.toSet());

        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("search", "");
        model.addAttribute("editSubject", subject);
        model.addAttribute("selectedCourseIds", selectedCourseIds);
        return "admin/subjects";
    }

    // Create a new subject record associated with courses
    @PostMapping
    public String create(
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam Integer units,
            @RequestParam Double lecHours,
            @RequestParam Double labHours,
            @RequestParam Double tuitionHours,
            @RequestParam(required = false) List<Long> courseIds,
            RedirectAttributes redirectAttributes) {

        // Check if the subject code already exists to prevent duplicates
        if (subjectService.findByCode(code).isPresent()) {
            redirectAttributes.addFlashAttribute("error",
                    "Subject code " + code + " already exists.");
            return "redirect:/admin/subjects";
        }

        // Create a new subject with the provided details
        Subject subject = new Subject();
        subject.setCode(code);
        subject.setName(name);
        subject.setUnits(units);
        subject.setLecHours(lecHours);
        subject.setLabHours(labHours);
        subject.setTuitionHours(tuitionHours);

        // Associate the subject with selected courses
        if (courseIds != null) {
            List<Course> courses = courseIds.stream()
                    .map(courseId -> courseService.findById(courseId).orElse(null))
                    .filter(c -> c != null)
                    .collect(Collectors.toList());
            subject.setCourses(new ArrayList<>(courses));
        }

        subjectService.save(subject);
        // Notify admin of successful creation with subject details
        redirectAttributes.addFlashAttribute("success",
                "Subject " + code + " - " + name + " added.");
        return "redirect:/admin/subjects";
    }

    // Update an existing subject record and its course associations
    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Integer units,
            @RequestParam Double lecHours,
            @RequestParam Double labHours,
            @RequestParam Double tuitionHours,
            @RequestParam(required = false) List<Long> courseIds,
            RedirectAttributes redirectAttributes) {

        // Retrieve the subject by ID, throwing an exception if not found
        Subject subject = subjectService.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));

        // Update the subject's core information
        subject.setName(name);
        subject.setUnits(units);
        subject.setLecHours(lecHours);
        subject.setLabHours(labHours);
        subject.setTuitionHours(tuitionHours);

        // Update course associations by clearing old ones and adding new ones
        subject.getCourses().clear();
        if (courseIds != null) {
            for (Long courseId : courseIds) {
                courseService.findById(courseId)
                        .ifPresent(subject.getCourses()::add);
            }
        }

        subjectService.save(subject);
        // Notify admin of successful update
        redirectAttributes.addFlashAttribute("success", "Subject updated.");
        return "redirect:/admin/subjects";
    }

    // Delete a subject record from the database
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        // Remove the subject record
        subjectService.delete(id);
        // Notify admin of successful deletion
        redirectAttributes.addFlashAttribute("success", "Subject deleted.");
        return "redirect:/admin/subjects";
    }
}