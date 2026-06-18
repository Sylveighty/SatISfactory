package com.pup.sis.controller;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Grade;
import com.pup.sis.entity.Schedule;
import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.Subject;
import com.pup.sis.entity.User;
import com.pup.sis.service.FacultyService;
import com.pup.sis.service.GradeService;
import com.pup.sis.service.ScheduleService;
import com.pup.sis.service.SectionService;
import com.pup.sis.service.StudentService;
import com.pup.sis.service.SubjectService;
import com.pup.sis.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/faculty")
public class FacultyPortalController {

    private final GradeService gradeService;
    private final FacultyService facultyService;
    private final SubjectService subjectService;
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final SectionService sectionService;
    private final StudentService studentService;

    public FacultyPortalController(
            GradeService gradeService,
            FacultyService facultyService,
            SubjectService subjectService,
            UserService userService,
            ScheduleService scheduleService,
            SectionService sectionService,
            StudentService studentService) {
        this.gradeService = gradeService;
        this.facultyService = facultyService;
        this.subjectService = subjectService;
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

    @GetMapping("/grades")
    public String grades(
            Authentication authentication,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long sectionId,
            Model model) {

        User user = userService.findByUsername(
                authentication.getName())
                .orElseThrow();

        Faculty faculty = facultyService.findByUser(user)
                .orElseThrow();

        List<Schedule> schedules =
                scheduleService.findByFaculty(faculty);

        model.addAttribute("schedules", schedules);

        if (subjectId != null && sectionId != null) {

            Section section =
                    sectionService.findById(sectionId)
                            .orElseThrow();

            Subject subject =
                    subjectService.findById(subjectId)
                            .orElseThrow();

            List<Student> students =
                    studentService.findBySection(section);

            model.addAttribute("selectedSection", section);
            model.addAttribute("selectedSubject", subject);
            model.addAttribute("students", students);
        }

        return "faculty/grades";
    }

    @GetMapping("/grade-sections")
    @ResponseBody
    public List<Section> gradeSections(
            Authentication authentication,
            @RequestParam Long subjectId) {

        User user = userService.findByUsername(
                authentication.getName())
                .orElseThrow();

        Faculty faculty = facultyService.findByUser(user)
                .orElseThrow();

        return scheduleService.findByFaculty(faculty)
                .stream()
                .filter(schedule ->
                        schedule.getSubject()
                                .getId()
                                .equals(subjectId))
                .map(Schedule::getSection)
                .distinct()
                .toList();
    }

    @PostMapping("/grades")
    public String saveGrades(
            Authentication authentication,
            @RequestParam Long subjectId,
            @RequestParam Long sectionId,
            @RequestParam Map<String, String> formData,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(
                authentication.getName())
                .orElseThrow();

        Faculty faculty = facultyService.findByUser(user)
                .orElseThrow();

        Subject subject =
                subjectService.findById(subjectId)
                        .orElseThrow();

        Section section =
                sectionService.findById(sectionId)
                        .orElseThrow();

        List<Student> students =
                studentService.findBySection(section);

        for (Student student : students) {

            String gradeValue =
                    formData.get(
                            "grade_" + student.getId());

            if (gradeValue == null ||
                    gradeValue.isBlank()) {
                continue;
            }

            Grade grade =
                    gradeService.findByStudentSubjectAndTerm(
                            student,
                            subject,
                            "2025-2026",
                            "First Semester")
                            .orElseGet(Grade::new);

            grade.setStudent(student);
            grade.setSubject(subject);
            grade.setSection(section);
            grade.setFaculty(faculty);

            grade.setSchoolYear("2025-2026");
            grade.setSemester("First Semester");

            grade.setFinalGrade(gradeValue);
            grade.setGradeStatus(
                    gradeService.deriveStatus(
                            gradeValue));

            gradeService.save(grade);
        }

        redirectAttributes.addFlashAttribute(
                "success",
                "Grades saved successfully.");

        return "redirect:/faculty/grades?subjectId="
                + subjectId
                + "&sectionId="
                + sectionId;
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