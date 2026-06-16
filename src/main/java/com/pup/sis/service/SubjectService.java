package com.pup.sis.service;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Subject;
import com.pup.sis.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    public Optional<Subject> findByCode(String code) {
        return subjectRepository.findByCode(code);
    }

    // Used by the curriculum page
    public List<Subject> findByCourse(Course course) {
        return subjectRepository.findByCoursesContaining(course);
    }

    // Used by the admin subject search bar
    public List<Subject> search(String query) {
        return subjectRepository
            .findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(query, query);
    }

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void delete(Long id) {
        subjectRepository.deleteById(id);
    }
}