package com.pup.sis.controller;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Schedule;
import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import com.pup.sis.service.FacultyService;
import com.pup.sis.service.ScheduleService;
import com.pup.sis.service.SectionService;
import com.pup.sis.service.StudentService;
import com.pup.sis.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/faculty")
public class FacultyPortalController {

    private final FacultyService facultyService;
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final SectionService sectionService;
    private final StudentService studentService;

    public FacultyPortalController(
            FacultyService facultyService,
            UserService userService,
            ScheduleService scheduleService,
            SectionService sectionService,
            StudentService studentService) {
        this.facultyService = facultyService;
        this.userService = userService;
        this.scheduleService = scheduleService;
        this.sectionService = sectionService;
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + auth.getName()));

        Faculty faculty = facultyService.findByUser(user).orElse(null);
        model.addAttribute("faculty", faculty);
        return "faculty/profile";
    }

    @GetMapping("/schedules")
    public String schedules(
            Authentication authentication,
            Model model) {

        // load faculty schedules

        return "faculty/schedules";
    }

    @GetMapping("/classes")
    public String classes(
            Authentication authentication,
            Model model) {

        User user = userService.findByUsername(
                authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Faculty faculty = facultyService.findByUser(user)
                .orElseThrow(() ->
                        new IllegalArgumentException("Faculty not found"));

        List<Schedule> schedules =
                scheduleService.findByFaculty(faculty);

        model.addAttribute("schedules", schedules);

        return "faculty/classes";
    }

    @GetMapping("/classes/{sectionId}")
    public String classDetail(
            @PathVariable Long sectionId,
            Model model) {

        Section section = sectionService.findById(sectionId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Section not found"));

        List<Student> students =
                studentService.findBySection(section);

        model.addAttribute("section", section);
        model.addAttribute("students", students);

        return "faculty/class-detail";
    }
}