package com.pup.sis.controller;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.Schedule;
import com.pup.sis.entity.Subject;
import com.pup.sis.service.CourseService;
import com.pup.sis.service.ScheduleService;
import com.pup.sis.service.SubjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SubjectService subjectService;
    private final CourseService courseService;
    private final ScheduleService scheduleService;

    public ApiController(
            SubjectService subjectService,
            CourseService courseService,
            ScheduleService scheduleService) {
        this.subjectService = subjectService;
        this.courseService = courseService;
        this.scheduleService = scheduleService;
    }

    // Used by schedule modal to filter subjects when a section is selected
    @GetMapping("/subjects")
    public List<Map<String, Object>> subjectsByCourse(@RequestParam Long courseId) {
        Course course = courseService.findById(courseId).orElse(null);
        if (course == null) return List.of();

        return subjectService.findByCourse(course).stream()
                .map(sub -> Map.<String, Object>of(
                        "id", sub.getId(),
                        "code", sub.getCode(),
                        "name", sub.getName()
                ))
                .toList();
    }

    // Used by faculty grade encoding to find sections this faculty
    // teaches a given subject in
    @GetMapping("/sections")
    public List<Map<String, Object>> sectionsByFacultyAndSubject(
            @RequestParam Long facultyId,
            @RequestParam Long subjectId) {

        List<Schedule> schedules = scheduleService.findAll();

        return schedules.stream()
                .filter(s -> s.getFaculty().getId().equals(facultyId))
                .filter(s -> s.getSubject().getId().equals(subjectId))
                .map(Schedule::getSection)
                .distinct()
                .map(sec -> Map.<String, Object>of(
                        "id", sec.getId(),
                        "name", sec.getSectionName()
                ))
                .toList();
    }
}