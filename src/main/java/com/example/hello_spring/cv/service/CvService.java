package com.example.hello_spring.cv.service;

import com.example.hello_spring.cv.dto.CvResponse;
import com.example.hello_spring.cv.exception.CvAccessDeniedException;
import com.example.hello_spring.cv.exception.CvNotFoundException;
import com.example.hello_spring.cv.model.Cv;
import com.example.hello_spring.cv.repository.CvRepository;
import com.example.hello_spring.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CvService {

    private final CvRepository cvRepository;

    public CvService(CvRepository cvRepository) {
        this.cvRepository = cvRepository;
    }

    // =========================
    // READ – GET MY CVS (DTO)
    // =========================
    @Transactional(readOnly = true)
    public List<CvResponse> getMyCvs(User user) {
        return cvRepository.findByOwner(user)
                .stream()
                .map(cv -> new CvResponse(
                        cv.getId(),
                        cv.getTitle(),
                        cv.getFileName(),
                        cv.getFileType(),
                        cv.getFilePath(),
                        cv.getUploadedAt(),
                        cv.getOwner().getId(),
                        cv.getOwner().getUsername()
                ))
                .toList();
    }

    // =========================
    // READ – INTERNAL USE (ENTITY)
    // =========================
    @Transactional(readOnly = true)
    public Cv getMyCvById(Long id, User user) {

        Cv cv = cvRepository.findById(id)
                .orElseThrow(() -> new CvNotFoundException("CV not found"));

        if (!cv.getOwner().getId().equals(user.getId())) {
            throw new CvAccessDeniedException("You do not own this CV");
        }

        return cv;
    }

    // =========================
    // CREATE
    // =========================
    @Transactional
    public Cv save(Cv cv, User owner) {

        cv.assignOwner(owner);

        // TEMP: simulate file storage path
        cv.setFilePath("/uploads/" + cv.getFileName());

        return cvRepository.save(cv);
    }

    // =========================
    // DELETE (OWNERSHIP ENFORCED)
    // =========================
    @Transactional
    public void delete(Long id, User user) {

        Cv cv = cvRepository.findById(id)
                .orElseThrow(() -> new CvNotFoundException("CV not found"));

        if (!cv.getOwner().getId().equals(user.getId())) {
            throw new CvAccessDeniedException("You do not own this CV");
        }

        cvRepository.delete(cv);
    }
}
