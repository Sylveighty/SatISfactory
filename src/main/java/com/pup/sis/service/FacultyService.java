package com.pup.sis.service;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.User;
import com.pup.sis.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public List<Faculty> findAll() {
        return facultyRepository.findAll();
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

    public void delete(Long id) {
        facultyRepository.deleteById(id);
    }
}