package com.pup.sis.controller;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import com.pup.sis.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final SubjectService subjectService;
    private final SectionService sectionService;
    private final ScheduleService scheduleService;
    private final GradeService gradeService;
    private final UserService userService;

    public DashboardController(
            StudentService studentService,
            FacultyService facultyService,
            SubjectService subjectService,
            SectionService sectionService,
            ScheduleService scheduleService,
            GradeService gradeService,
            UserService userService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.subjectService = subjectService;
        this.sectionService = sectionService;
        this.scheduleService = scheduleService;
        this.gradeService = gradeService;
        this.userService = userService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("totalStudents", studentService.findAll().size());
        model.addAttribute("totalFaculty",  facultyService.findAll().size());
        model.addAttribute("totalSubjects", subjectService.findAll().size());
        model.addAttribute("totalSections", sectionService.findAll().size());
        return "admin/dashboard";
    }

    @GetMapping("/faculty/dashboard")
    public String facultyDashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());

        User user = userService.findByUsername(auth.getName()).orElse(null);
        Faculty faculty = (user != null) ? facultyService.findByUser(user).orElse(null) : null;
        model.addAttribute("faculty", faculty);

        if (faculty != null) {
            List<?> schedules = scheduleService.findByFacultyAndTerm(
                    faculty,
                    FacultyPortalController.CURRENT_YEAR,
                    FacultyPortalController.CURRENT_SEM);
            model.addAttribute("schedules", schedules);
            model.addAttribute("classCount", schedules.size());

            long studentsToGrade = scheduleService
                    .findByFacultyAndTerm(faculty,
                            FacultyPortalController.CURRENT_YEAR,
                            FacultyPortalController.CURRENT_SEM)
                    .stream()
                    .mapToLong(s -> studentService.findBySection(s.getSection()).size())
                    .sum();
            model.addAttribute("studentsToGrade", studentsToGrade);
        } else {
            model.addAttribute("schedules", List.of());
            model.addAttribute("classCount", 0);
            model.addAttribute("studentsToGrade", 0);
        }

        model.addAttribute("schoolYear", FacultyPortalController.CURRENT_YEAR);
        model.addAttribute("semester",   FacultyPortalController.CURRENT_SEM);
        return "faculty/dashboard";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());

        User user = userService.findByUsername(auth.getName()).orElse(null);
        Student student = (user != null) ? studentService.findByUser(user).orElse(null) : null;
        model.addAttribute("student", student);

        if (student != null) {
            var grades = gradeService.findByStudentAndTerm(
                    student,
                    FacultyPortalController.CURRENT_YEAR,
                    FacultyPortalController.CURRENT_SEM);
            model.addAttribute("grades", grades);
            model.addAttribute("gpa", gradeService.calculateGPA(grades));

            if (student.getSection() != null) {
                var enrolledSubjectIds = grades.stream()
                        .map(g -> g.getSubject().getId())
                        .toList();
                var filteredSchedule = scheduleService.findBySectionAndTerm(
                        student.getSection(),
                        FacultyPortalController.CURRENT_YEAR,
                        FacultyPortalController.CURRENT_SEM).stream()
                        .filter(sc -> enrolledSubjectIds.contains(sc.getSubject().getId()))
                        .toList();
                model.addAttribute("schedule", filteredSchedule);
            } else {
                model.addAttribute("schedule", List.of());
            }
        } else {
            model.addAttribute("grades",   List.of());
            model.addAttribute("schedule", List.of());
            model.addAttribute("gpa", null);
        }

        model.addAttribute("schoolYear", FacultyPortalController.CURRENT_YEAR);
        model.addAttribute("semester",   FacultyPortalController.CURRENT_SEM);
        return "student/dashboard";
    }
}