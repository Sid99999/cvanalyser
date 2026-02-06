package com.example.hello_spring.cv.service;

import com.example.hello_spring.cv.dto.CvResponse;
import com.example.hello_spring.cv.dto.FileDownload;
import com.example.hello_spring.cv.exception.CvAccessDeniedException;
import com.example.hello_spring.cv.exception.CvNotFoundException;
import com.example.hello_spring.cv.exception.InvalidFileException;
import com.example.hello_spring.cv.model.Cv;
import com.example.hello_spring.cv.repository.CvRepository;
import com.example.hello_spring.model.User;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Service
public class CvService {

    // =========================
    // FILE VALIDATION RULES
    // =========================
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

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
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // READ – INTERNAL (ENTITY, OWNER ONLY)
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
    // CREATE – UPLOAD CV (MULTIPART)
    // =========================
    @Transactional
    public CvResponse uploadCv(MultipartFile file, String title, User owner) {

        validateFile(file);

        Cv cv = Cv.create(
                title,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                owner
        );

        // TEMP placeholder — real path is set after file storage
        cv.setFilePath("PENDING");

        Cv savedCv = cvRepository.save(cv);

        return toResponse(savedCv);
    }

    // =========================
    // DOWNLOAD – OWNER ONLY (PART 5)
    // =========================
    @Transactional(readOnly = true)
    public FileDownload downloadCv(Long cvId, User user) {

        Cv cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new CvNotFoundException("CV not found"));

        if (!cv.getOwner().getId().equals(user.getId())) {
            throw new CvAccessDeniedException("You do not own this CV");
        }

        Path path = Path.of(cv.getFilePath());

        if (!Files.exists(path)) {
            throw new CvNotFoundException("CV file not found on server");
        }

        try {
            Resource resource = new UrlResource(path.toUri());

            return new FileDownload(
                    resource,
                    cv.getFileName(),
                    cv.getFileType()
            );

        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to load CV file", e);
        }
    }

    // =========================
    // DELETE – OWNER ONLY
    // =========================
    @Transactional
    public void delete(Long id, User user) {

        Cv cv = cvRepository.findById(id)
                .orElseThrow(() -> new CvNotFoundException("CV not found"));

        if (!cv.getOwner().getId().equals(user.getId())) {
            throw new CvAccessDeniedException("You do not own this CV");
        }

        // 1️⃣ Delete file from disk first
        deleteFileSafely(cv.getFilePath());

        // 2️⃣ Delete DB record
        cvRepository.delete(cv);
    }

    private void deleteFileSafely(String filePath) {
        try {
            Path path = Path.of(filePath);

            if (Files.exists(path)) {
                Files.delete(path);
            }

        } catch (Exception ex) {
            // VERY IMPORTANT:
            // If file deletion fails, we abort DB deletion
            throw new RuntimeException("Failed to delete CV file from disk", ex);
        }
    }

    // =========================
    // FILE VALIDATION (PART 4)
    // =========================
    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size exceeds 5MB limit");
        }

        if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("Unsupported file type");
        }
    }

    // =========================
    // MAPPER – ENTITY → DTO
    // =========================
    private CvResponse toResponse(Cv cv) {

        return new CvResponse(
                cv.getId(),
                cv.getTitle(),
                cv.getFileName(),
                cv.getFileType(),
                cv.getFilePath(),
                cv.getUploadedAt(),
                cv.getOwner().getId(),
                cv.getOwner().getUsername()
        );
    }
}
