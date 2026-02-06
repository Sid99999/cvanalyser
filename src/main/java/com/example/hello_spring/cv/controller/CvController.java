package com.example.hello_spring.cv.controller;

import com.example.hello_spring.cv.dto.CvResponse;
import com.example.hello_spring.cv.dto.FileDownload;
import com.example.hello_spring.cv.service.CvService;
import com.example.hello_spring.model.User;
import com.example.hello_spring.service.UserService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cvs")
public class CvController {

    private final CvService cvService;
    private final UserService userService;

    public CvController(CvService cvService, UserService userService) {
        this.cvService = cvService;
        this.userService = userService;
    }

    // =========================
    // GET – All CVs of logged-in user
    // =========================
    @GetMapping
    public ResponseEntity<List<CvResponse>> getMyCvs(Authentication authentication) {

        User user = userService.getByUsername(authentication.getName());
        List<CvResponse> cvs = cvService.getMyCvs(user);

        return ResponseEntity.ok(cvs);
    }

    // =========================
    // POST – Upload a new CV (Multipart)
    // =========================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CvResponse> uploadCv(
            @RequestPart("file") MultipartFile file,
            @RequestPart("title") String title,
            Authentication authentication
    ) {

        User user = userService.getByUsername(authentication.getName());
        CvResponse response = cvService.uploadCv(file, title, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================
    // GET – Download CV (OWNER ONLY) ✅ PART 5
    // =========================
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadCv(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User user = userService.getByUsername(authentication.getName());
        FileDownload download = cvService.downloadCv(id, user);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + download.getFileName() + "\""
                )
                .body(download.getResource());
    }

    // =========================
    // DELETE – Delete CV (OWNER ONLY)
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCv(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User user = userService.getByUsername(authentication.getName());
        cvService.delete(id, user);

        return ResponseEntity.noContent().build();
    }
}
