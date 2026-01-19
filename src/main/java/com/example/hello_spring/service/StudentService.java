package com.example.hello_spring.service;

import com.example.hello_spring.dto.StudentRequest;
import com.example.hello_spring.dto.StudentResponse;
import com.example.hello_spring.model.Student;
import com.example.hello_spring.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.hello_spring.exception.StudentNotFoundException;
import com.example.hello_spring.exception.DuplicateEmailException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

@Service
public class StudentService {
    private static final Logger log = LoggerFactory.getLogger(StudentService.class);


    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    // --------------------
    // MAPPING METHODS
    // --------------------

    private Student toEntity(StudentRequest request) {
        return new Student(
                request.getName(),
                request.getEmail(),
                request.getAge()
        );
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getAge(),
                student.getCreatedAt()
        );
    }

    // --------------------
    // SERVICE METHODS
    // --------------------

    // CREATE
    @Transactional
    public StudentResponse create(StudentRequest request) {

        log.info("Creating student with email={}", request.getEmail());

        repo.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    log.warn("Duplicate email attempt: {}", request.getEmail());
                    throw new DuplicateEmailException(request.getEmail());
                });

        Student student = toEntity(request);
        Student saved = repo.save(student);

        log.info("Student created with id={}", saved.getId());

        return toResponse(saved);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public Page<StudentResponse> getAll(Pageable pageable) {
        return repo.findAll(pageable)
                .map(this::toResponse);
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {

        log.info("Fetching student with id={}", id);

        Student student = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Student not found with id={}", id);
                    return new StudentNotFoundException(id);
                });

        return toResponse(student);
    }

    // UPDATE
    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = repo.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setAge(request.getAge());

        Student updated = repo.save(student);
        return toResponse(updated);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        log.info("Deleting student with id={}", id);
        repo.deleteById(id);
    }

}
