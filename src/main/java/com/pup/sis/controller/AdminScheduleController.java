package com.pup.sis.controller;

import com.pup.sis.entity.*;
import com.pup.sis.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;

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
    public String list(
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) Long facultyId,
            Model model) {

        var schedules = scheduleService.findAll();

        if (sectionId != null) {
            schedules = schedules.stream()
                    .filter(s -> s.getSection().getId().equals(sectionId)).toList();
        }
        if (facultyId != null) {
            schedules = schedules.stream()
                    .filter(s -> s.getFaculty().getId().equals(facultyId)).toList();
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("faculty", facultyService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("selectedSectionId", sectionId);
        model.addAttribute("selectedFacultyId", facultyId);
        return "admin/schedules";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Schedule schedule = scheduleService.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));

        model.addAttribute("schedules", scheduleService.findAll());
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("faculty", facultyService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("editSchedule", schedule);
        return "admin/schedules";
    }

    @PostMapping
    public String create(
            @RequestParam Long sectionId,
            @RequestParam Long subjectId,
            @RequestParam Long facultyId,
            @RequestParam String day,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) String room,
            @RequestParam String schoolYear,
            @RequestParam String semester,
            RedirectAttributes redirectAttributes) {

        Schedule schedule = new Schedule();
        sectionService.findById(sectionId).ifPresent(schedule::setSection);
        subjectService.findById(subjectId).ifPresent(schedule::setSubject);
        facultyService.findById(facultyId).ifPresent(schedule::setFaculty);
        schedule.setDay(day);
        schedule.setStartTime(LocalTime.parse(startTime));
        schedule.setEndTime(LocalTime.parse(endTime));
        schedule.setRoom(room);
        schedule.setSchoolYear(schoolYear);
        schedule.setSemester(semester);

        String conflict = scheduleService.checkConflict(schedule);
        scheduleService.save(schedule);

        if (conflict != null) {
            redirectAttributes.addFlashAttribute("warning", conflict);
        } else {
            redirectAttributes.addFlashAttribute("success", "Schedule added.");
        }
        return "redirect:/admin/schedules";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam Long sectionId,
            @RequestParam Long subjectId,
            @RequestParam Long facultyId,
            @RequestParam String day,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) String room,
            @RequestParam String schoolYear,
            @RequestParam String semester,
            RedirectAttributes redirectAttributes) {

        Schedule schedule = scheduleService.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));

        sectionService.findById(sectionId).ifPresent(schedule::setSection);
        subjectService.findById(subjectId).ifPresent(schedule::setSubject);
        facultyService.findById(facultyId).ifPresent(schedule::setFaculty);
        schedule.setDay(day);
        schedule.setStartTime(LocalTime.parse(startTime));
        schedule.setEndTime(LocalTime.parse(endTime));
        schedule.setRoom(room);
        schedule.setSchoolYear(schoolYear);
        schedule.setSemester(semester);

        String conflict = scheduleService.checkConflict(schedule);
        scheduleService.save(schedule);

        if (conflict != null) {
            redirectAttributes.addFlashAttribute("warning", conflict);
        } else {
            redirectAttributes.addFlashAttribute("success", "Schedule updated.");
        }
        return "redirect:/admin/schedules";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        scheduleService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Schedule deleted.");
        return "redirect:/admin/schedules";
    }
}