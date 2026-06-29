package com.pup.sis.service;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.User;
import com.pup.sis.repository.FacultyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    // Only returns active (enabled) faculty — deactivated ones are excluded from the list
    public List<Faculty> findAll() {
        return facultyRepository.findByUserEnabled(true);
    }

    public Optional<Faculty> findById(Long id) {
        return facultyRepository.findById(id);
    }

    public Optional<Faculty> findByUser(User user) {
        return facultyRepository.findByUser(user);
    }

    public Optional<Faculty> findByFacultyId(String facultyId) {
        return facultyRepository.findByFacultyId(facultyId);
    }

    public Faculty save(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    // Soft delete: disables the faculty's login account without removing any records.
    // Their schedule assignments and grade records are all preserved.
    @Transactional
    public void deactivate(Long id) {
        facultyRepository.findById(id).ifPresent(faculty -> {
            if (faculty.getUser() != null) {
                faculty.getUser().setEnabled(false);
                facultyRepository.save(faculty);
            }
        });
    }

    // Hard delete — kept for emergencies (e.g. duplicate/test records)
    public void delete(Long id) {
        facultyRepository.deleteById(id);
    }
}