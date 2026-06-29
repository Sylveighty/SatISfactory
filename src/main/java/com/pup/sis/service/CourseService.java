package com.pup.sis.service;

import com.pup.sis.entity.Course;
import com.pup.sis.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public boolean courseCodeExists(String code) {
    return courseRepository.existsByCode(code);
    }

public boolean courseCodeExistsForAnotherCourse(Long id, String code) {
    Optional<Course> existing = courseRepository.findByCode(code);

    return existing.isPresent() && !existing.get().getId().equals(id);
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }
    public void deleteById(Long id) {
    courseRepository.deleteById(id);
    }
}