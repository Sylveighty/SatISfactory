package com.pup.sis.service;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Section;
import com.pup.sis.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<Section> findAll() {
        return sectionRepository.findAll();
    }

    public Optional<Section> findById(Long id) {
        return sectionRepository.findById(id);
    }

    public List<Section> findByCourse(Course course) {
        return sectionRepository.findByCourse(course);
    }

    public List<Section> findByCourseAndYearLevel(Course course, Integer yearLevel) {
        return sectionRepository.findByCourseAndYearLevel(course, yearLevel);
    }

    public Section save(Section section) {
        return sectionRepository.save(section);
    }

    public void delete(Long id) {
        sectionRepository.deleteById(id);
    }
}