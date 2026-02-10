package com.example.hello_spring.cv.controller;

import com.example.hello_spring.cv.analysis.service.CvAnalysisService;
import com.example.hello_spring.cv.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CvAnalysisController {

    private final CvAnalysisService cvAnalysisService;

    public CvAnalysisController(CvAnalysisService cvAnalysisService) {
        this.cvAnalysisService = cvAnalysisService;
    }

    // ===============================
    // PART 1 – Trigger analysis
    // ===============================
    @PostMapping("/cvs/{cvId}/analysis")
    public ResponseEntity<CvAnalysisResponse> triggerAnalysis(
            @PathVariable Long cvId,
            @RequestBody(required = false) CvAnalysisRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(
                        cvAnalysisService.startAnalysis(
                                cvId,
                                request,
                                userDetails.getUsername()
                        )
                );
    }

    // ===============================
    // PART 2 – Get analysis by ID
    // ===============================
    @GetMapping("/analyses/{analysisId}")
    public ResponseEntity<CvAnalysisResultResponse> getAnalysisById(
            @PathVariable Long analysisId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                cvAnalysisService.getAnalysisById(
                        analysisId,
                        userDetails.getUsername()
                )
        );
    }

    // ===============================
    // PART 2 – Latest analysis for CV
    // ===============================
    @GetMapping("/cvs/{cvId}/analysis/latest")
    public ResponseEntity<CvAnalysisResultResponse> getLatestAnalysisForCv(
            @PathVariable Long cvId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                cvAnalysisService.getLatestAnalysisForCv(
                        cvId,
                        userDetails.getUsername()
                )
        );
    }

    // ===============================
    // PART 3 – Retry failed analysis
    // ===============================
    @PostMapping("/analyses/{analysisId}/retry")
    public ResponseEntity<CvAnalysisResponse> retryAnalysis(
            @PathVariable Long analysisId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(
                        cvAnalysisService.retryAnalysis(
                                analysisId,
                                userDetails.getUsername()
                        )
                );
    }

    // ===============================
    // PART 4 – History by CV
    // ===============================
    @GetMapping("/cvs/{cvId}/analyses")
    public ResponseEntity<List<CvAnalysisHistoryItem>> getAnalysisHistoryForCv(
            @PathVariable Long cvId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                cvAnalysisService.getAnalysisHistoryForCv(
                        cvId,
                        userDetails.getUsername()
                )
        );
    }

    // ===============================
    // PART 4 – History by User
    // ===============================
    @GetMapping("/analyses")
    public ResponseEntity<List<CvAnalysisHistoryItem>> getAnalysisHistoryForUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                cvAnalysisService.getAnalysisHistoryForUser(
                        userDetails.getUsername()
                )
        );
    }
}
