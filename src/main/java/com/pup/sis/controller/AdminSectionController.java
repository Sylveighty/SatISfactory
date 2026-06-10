package com.pup.sis.controller;

import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.service.CourseService;
import com.pup.sis.service.SectionService;
import com.pup.sis.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/sections")
public class AdminSectionController {

    private final SectionService sectionService;
    private final CourseService courseService;
    private final StudentService studentService;

    public AdminSectionController(
            SectionService sectionService,
            CourseService courseService,
            StudentService studentService) {
        this.sectionService = sectionService;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    // Helper method to build a map of student counts for each section
    // This is used by all GET methods to display section capacities
    private Map<Long, Long> buildStudentCounts(List<Section> sections) {
        Map<Long, Long> counts = new HashMap<>();
        for (Section s : sections) {
            counts.put(s.getId(), studentService.countBySection(s));
        }
        return counts;
    }

    // Retrieve and display all sections with student count information

    @GetMapping
    public String list(Model model) {
        List<Section> sections = sectionService.findAll();
        model.addAttribute("sections", sections);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("studentCounts", buildStudentCounts(sections));
        return "admin/sections";
    }

    // Display the edit form for a specific section record
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        // Retrieve the section by ID, throwing an exception if not found
        Section section = sectionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found: " + id));

        // Populate the model with all sections, courses, and student counts
        List<Section> sections = sectionService.findAll();
        model.addAttribute("sections", sections);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("studentCounts", buildStudentCounts(sections));
        // Mark this section as the one being edited in the UI
        model.addAttribute("editSection", section);
        return "admin/sections";
    }

    // Display the form for assigning students to a section
    @GetMapping("/{id}/assign")
    public String assignForm(@PathVariable Long id, Model model) {
        // Retrieve the section by ID, throwing an exception if not found
        Section section = sectionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found: " + id));

        // Find all students who are eligible for this section
        // Eligibility is based on matching course and year level
        List<Student> eligible = (section.getCourse() != null && section.getYearLevel() != null)
                ? studentService.findByCourseAndYearLevel(
                        section.getCourse(), section.getYearLevel())
                : List.of();

        // Identify students already assigned to this section for pre-checking checkboxes
        Set<Long> assignedIds = eligible.stream()
                .filter(s -> s.getSection() != null
                        && s.getSection().getId().equals(id))
                .map(Student::getId)
                .collect(Collectors.toSet());

        List<Section> sections = sectionService.findAll();
        model.addAttribute("sections", sections);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("studentCounts", buildStudentCounts(sections));
        model.addAttribute("assignSection", section);
        model.addAttribute("eligibleStudents", eligible);
        model.addAttribute("assignedStudentIds", assignedIds);
        return "admin/sections";
    }

    // Create a new section record
    @PostMapping
    public String create(
            @RequestParam String sectionName,
            @RequestParam Long courseId,
            @RequestParam Integer yearLevel,
            RedirectAttributes redirectAttributes) {

        // Create a new section with the provided details
        Section section = new Section();
        section.setSectionName(sectionName);
        section.setYearLevel(yearLevel);
        // Assign the selected course to the section
        courseService.findById(courseId).ifPresent(section::setCourse);
        sectionService.save(section);

        // Notify admin of successful creation
        redirectAttributes.addFlashAttribute("success",
                "Section " + sectionName + " created.");
        return "redirect:/admin/sections";
    }

    // Update an existing section record
    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String sectionName,
            @RequestParam Long courseId,
            @RequestParam Integer yearLevel,
            RedirectAttributes redirectAttributes) {

        // Retrieve the section by ID, throwing an exception if not found
        Section section = sectionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found: " + id));

        // Update the section's information
        section.setSectionName(sectionName);
        section.setYearLevel(yearLevel);
        // Update the course assignment if a new course is selected
        courseService.findById(courseId).ifPresent(section::setCourse);
        sectionService.save(section);

        // Notify admin of successful update
        redirectAttributes.addFlashAttribute("success", "Section updated.");
        return "redirect:/admin/sections";
    }

    // Delete a section record and unassign all students
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        // Retrieve the section by ID, throwing an exception if not found
        Section section = sectionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found: " + id));

        // Unassign all students from this section before deleting
        // This ensures referential integrity and prevents orphaned student records
        List<Student> assigned = studentService.findBySection(section);
        for (Student student : assigned) {
            student.setSection(null);
            studentService.save(student);
        }

        // Delete the section record from the database
        sectionService.delete(id);
        // Notify admin of successful deletion
        redirectAttributes.addFlashAttribute("success", "Section deleted.");
        return "redirect:/admin/sections";
    }

    // Process student assignment to a section
    @PostMapping("/{id}/assign")
    public String assignStudents(
            @PathVariable Long id,
            @RequestParam(required = false) List<Long> studentIds,
            RedirectAttributes redirectAttributes) {

        Section section = sectionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found: " + id));

        List<Student> eligible = (section.getCourse() != null && section.getYearLevel() != null)
                ? studentService.findByCourseAndYearLevel(
                        section.getCourse(), section.getYearLevel())
                : List.of();

        for (Student student : eligible) {
            boolean shouldAssign = studentIds != null
                    && studentIds.contains(student.getId());
            boolean isInThisSection = student.getSection() != null
                    && student.getSection().getId().equals(id);

            if (shouldAssign) {
                student.setSection(section);
            } else if (isInThisSection) {
                // Unchecked - remove from this section
                student.setSection(null);
            }
            studentService.save(student);
        }

        redirectAttributes.addFlashAttribute("success",
                "Students assigned to " + section.getSectionName() + ".");
        return "redirect:/admin/sections";
    }
}