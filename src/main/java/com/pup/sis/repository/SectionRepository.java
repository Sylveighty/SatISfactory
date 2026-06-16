package com.pup.sis.repository;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    // List all sections for a given course
    List<Section> findByCourse(Course course);

    // List sections for a specific course and year level
    List<Section> findByCourseAndYearLevel(Course course, Integer yearLevel);
}