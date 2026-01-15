package com.example.hello_spring.service;

import com.example.hello_spring.model.Student;
import com.example.hello_spring.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    public List<Student> getAll() {
        return repo.findAll();
    }

    public Student getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Student not found: " + id));
    }

    @Transactional
    public Student create(Student student) {
        // in prod, check duplicate email etc.
        return repo.save(student);
    }

    @Transactional
    public Student update(Long id, Student input) {
        Student s = repo.findById(id).orElseThrow(() -> new RuntimeException("Student not found: " + id));
        s.setName(input.getName());
        s.setEmail(input.getEmail());
        s.setAge(input.getAge());
        return repo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
