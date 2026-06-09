package com.pup.sis.repository;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // List all sections for a given course
    List<Section> findByCourse(Course course);

    // List sections for a specific course and year level
    List<Section> findByCourseAndYearLevel(Course course, Integer yearLevel);
}