package com.pup.sis.service;

import com.pup.sis.entity.Faculty;
import com.pup.sis.entity.User;
import com.pup.sis.repository.FacultyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class FacultyChangePasswordService {

    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    public FacultyChangePasswordService(
            FacultyRepository facultyRepository,
            PasswordEncoder passwordEncoder) {

        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String changePassword(
            String facultyId,
            String currentPassword,
            String newPassword,
            String confirmPassword) {

        Optional<Faculty> facultyOptional = facultyRepository.findByFacultyId(facultyId);

        if (!facultyOptional.isPresent()) {
            return "Faculty not found.";
        }
        Faculty faculty = facultyOptional.get();

        User user = faculty.getUser();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "Current password is incorrect.";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "New password and confirmation do not match.";
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        facultyRepository.save(faculty);

        return "SUCCESS";
    }
}