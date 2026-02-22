package com.example.hello_spring.cv.service;

import com.example.hello_spring.cv.dto.CvResponse;
import com.example.hello_spring.cv.dto.FileDownload;
import com.example.hello_spring.cv.exception.CvAccessDeniedException;
import com.example.hello_spring.cv.exception.CvNotFoundException;
import com.example.hello_spring.cv.exception.InvalidFileException;
import com.example.hello_spring.cv.extraction.service.CvTextExtractionService;
import com.example.hello_spring.cv.model.Cv;
import com.example.hello_spring.cv.repository.CvRepository;
import com.example.hello_spring.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.UUID;

@Service
public class CvService {

    private static final Logger log =
            LoggerFactory.getLogger(CvService.class);

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final Path STORAGE_ROOT = Path.of("uploads/cvs");

    private final CvRepository cvRepository;
    private final CvTextExtractionService textExtractionService;

    public CvService(CvRepository cvRepository,
                     CvTextExtractionService textExtractionService) {
        this.cvRepository = cvRepository;
        this.textExtractionService = textExtractionService;
    }

    // =====================================================
    // GET USER CVS
    // =====================================================

    @Transactional(readOnly = true)
    public List<CvResponse> getMyCvs(User user) {
        log.info("Fetching CV list for user '{}'", user.getUsername());

        return cvRepository.findByOwner(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =====================================================
    // UPLOAD CV
    // =====================================================

    @Transactional
    public CvResponse uploadCv(MultipartFile file,
                               String title,
                               User owner) {

        log.info("User '{}' uploading CV '{}'", owner.getUsername(), title);

        validateFile(file);

        Cv cv = Cv.create(
                title,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                owner
        );

        cv.setFilePath("TEMP");
        Cv saved = cvRepository.save(cv);

        Path storedPath = storeFile(saved.getId(), file);
        saved.setFilePath(storedPath.toString());

        String extractedText = textExtractionService.extractText(
                storedPath.toFile(),
                saved.getFileType()
        );

        saved.setExtractedText(extractedText);

        Cv finalCv = cvRepository.save(saved);

        log.info("CV uploaded successfully. id={}", finalCv.getId());

        return toResponse(finalCv);
    }

    // =====================================================
    // DOWNLOAD CV
    // =====================================================

    @Transactional(readOnly = true)
    public FileDownload downloadCv(Long id, User user) {

        Cv cv = getOwnedCv(id, user);

        log.info("User '{}' downloading CV id={}", user.getUsername(), id);

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
            log.error("Failed loading file for CV id={}", id, e);
            throw new RuntimeException("Failed to load file", e);
        }
    }

    // =====================================================
    // DELETE CV
    // =====================================================

    @Transactional
    public void delete(Long id, User user) {

        Cv cv = getOwnedCv(id, user);

        log.warn("User '{}' deleting CV id={}", user.getUsername(), id);

        deleteFileSafely(cv.getFilePath());
        cvRepository.delete(cv);
    }

    // =====================================================
    // INTERNAL VALIDATION
    // =====================================================

    private Cv getOwnedCv(Long id, User user) {

        Cv cv = cvRepository.findById(id)
                .orElseThrow(() -> new CvNotFoundException("CV not found"));

        if (!cv.getOwner().getId().equals(user.getId())) {
            throw new CvAccessDeniedException("Access denied");
        }

        return cv;
    }

    // =====================================================
    // FILE STORAGE
    // =====================================================

    private Path storeFile(Long cvId, MultipartFile file) {

        try {
            Files.createDirectories(STORAGE_ROOT);

            String originalName = file.getOriginalFilename();

            if (originalName == null) {
                throw new InvalidFileException("Invalid file name");
            }

            String cleanedName = originalName
                    .replaceAll("[^a-zA-Z0-9.-]", "_");
            String safeName = cvId + "_" +
                    UUID.randomUUID() + "_" +
                    cleanedName;
            Path path = STORAGE_ROOT.resolve(safeName);

            Files.write(path, file.getBytes());

            return path;

        } catch (Exception ex) {
            log.error("File storage failed for CV id={}", cvId, ex);
            throw new RuntimeException("Failed to store file", ex);
        }
    }

    private void deleteFileSafely(String filePath) {

        try {
            Path path = Path.of(filePath);

            if (Files.exists(path)) {
                Files.delete(path);
            }

        } catch (Exception ex) {
            log.error("File deletion failed: {}", filePath, ex);
            throw new RuntimeException("Failed to delete file", ex);
        }
    }

    // =====================================================
    // VALIDATION
    // =====================================================

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File exceeds 5MB limit");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new InvalidFileException("Unsupported file type");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new InvalidFileException("Invalid file name");
        }

        String lower = originalName.toLowerCase();

        if (!(lower.endsWith(".pdf") || lower.endsWith(".docx"))) {
            throw new InvalidFileException("Only PDF or DOCX files are allowed");
        }

        if (lower.contains("..") || lower.contains("/")) {
            throw new InvalidFileException("Invalid file name");
        }
    }

    // =====================================================
    // MAPPER
    // =====================================================

    private CvResponse toResponse(Cv cv) {

        return new CvResponse(
                cv.getId(),
                cv.getTitle(),
                cv.getFileName(),
                cv.getFileType(),
                cv.getUploadedAt(),
                cv.getOwner().getId(),
                cv.getOwner().getUsername()
        );
    }
}
