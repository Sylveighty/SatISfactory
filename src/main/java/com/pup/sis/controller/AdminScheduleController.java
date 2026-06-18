package com.pup.sis.controller;

import com.pup.sis.entity.Section;
import com.pup.sis.entity.Subject;
import com.pup.sis.service.FacultyService;
import com.pup.sis.service.ScheduleService;
import com.pup.sis.service.SectionService;
import com.pup.sis.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/schedules")
public class AdminScheduleController {

    private final ScheduleService scheduleService;
    private final SectionService sectionService;
    private final SubjectService subjectService;
    private final FacultyService facultyService;

    public AdminScheduleController(
            ScheduleService scheduleService,
            SectionService sectionService,
            SubjectService subjectService,
            FacultyService facultyService) {

        this.scheduleService = scheduleService;
        this.sectionService = sectionService;
        this.subjectService = subjectService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public String schedules(
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) Long facultyId,
            Model model) {

        model.addAttribute("schedules",
                scheduleService.findAll());

        model.addAttribute("sections",
                sectionService.findAll());

        model.addAttribute("faculties",
                facultyService.findAll());

        return "admin/schedules";
    }

    @GetMapping("/subjects")
    @ResponseBody
    public List<Subject> getSubjectsBySection(
            @RequestParam Long sectionId) {

        Section section =
                sectionService.findById(sectionId)
                        .orElseThrow();

        return subjectService.findByCourse(
                section.getCourse());
    }
}