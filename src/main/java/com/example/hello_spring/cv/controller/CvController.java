package com.example.hello_spring.cv.controller;

import com.example.hello_spring.cv.dto.CvResponse;
import com.example.hello_spring.cv.model.Cv;
import com.example.hello_spring.cv.service.CvService;
import com.example.hello_spring.model.User;
import com.example.hello_spring.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    // GET all CVs of logged-in user
    // =========================
    @GetMapping
    public ResponseEntity<List<CvResponse>> getMyCvs(Authentication authentication) {

        String username = authentication.getName();
        User user = userService.getByUsername(username);

        List<CvResponse> cvs = cvService.getMyCvs(user);
        return ResponseEntity.ok(cvs);
    }

    // =========================
    // POST – Create a new CV
    // =========================
    @PostMapping
    public ResponseEntity<CvResponse> createCv(
            @RequestBody Cv cv,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);

        Cv savedCv = cvService.save(cv, user);

        CvResponse response = new CvResponse(
                savedCv.getId(),
                savedCv.getTitle(),
                savedCv.getFileName(),
                savedCv.getFileType(),
                savedCv.getFilePath(),
                savedCv.getUploadedAt(),
                user.getId(),
                user.getUsername()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================
    // DELETE – Delete a CV (OWNER ONLY)
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCv(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);

        cvService.delete(id, user);

        return ResponseEntity.noContent().build();
    }
}
