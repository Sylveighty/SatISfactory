package com.pup.sis.service;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import com.pup.sis.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Only returns active (enabled) students — deactivated ones are excluded from the list
    public List<Student> findAll() {
        return studentRepository.findByUserEnabled(true);
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> findByUser(User user) {
        return studentRepository.findByUser(user);
    }

    public Optional<Student> findByStudentNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber);
    }

    // Returns true only if the given student number is currently held by an
    // ACTIVE student. A deactivated student (e.g. a mis-encode that was
    // soft-deleted) does not block the number from being reused.
    public boolean isStudentNumberActive(String studentNumber) {
        return studentRepository.findActiveByStudentNumber(studentNumber).isPresent();
    }

    public List<Student> search(String query) {
        return studentRepository.findByFullNameContainingIgnoreCase(query);
    }

    public List<Student> findByCourseAndYearLevel(Course course, Integer yearLevel) {
        return studentRepository.findByCourseAndYearLevel(course, yearLevel);
    }

    public List<Student> findBySection(Section section) {
        return studentRepository.findBySection(section);
    }

    public long countBySection(Section section) {
        return studentRepository.countBySection(section);
    }

    @Transactional
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    // Soft delete: disables the student's login account without removing any records.
    // Grade history, enrollment data, and the student record itself are all preserved.
    // Their old studentNumber becomes reusable by a new student, since duplicate
    // checks only consider ACTIVE students (see isStudentNumberActive above).
    @Transactional
    public void deactivate(Long id) {
        studentRepository.findById(id).ifPresent(student -> {
            if (student.getUser() != null) {
                student.getUser().setEnabled(false);
                studentRepository.save(student);
            }
        });
    }

    // Hard delete — not exposed in the UI. Reserved for direct database
    // administration (e.g. via Workbench) for genuine mis-encodes with no
    // associated records. Kept here only as a service-layer capability.
    public void delete(Long id) {
        studentRepository.deleteById(id);
    }
}