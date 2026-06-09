package com.pup.sis.service;

import com.pup.sis.entity.Course;
import com.pup.sis.entity.Section;
import com.pup.sis.entity.Student;
import com.pup.sis.entity.User;
import com.pup.sis.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
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

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }
}