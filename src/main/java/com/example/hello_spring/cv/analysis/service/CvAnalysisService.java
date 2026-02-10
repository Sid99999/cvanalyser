package com.example.hello_spring.cv.analysis.service;

import com.example.hello_spring.cv.analysis.model.AnalysisStatus;
import com.example.hello_spring.cv.analysis.model.CvAnalysis;
import com.example.hello_spring.cv.analysis.repository.CvAnalysisRepository;
import com.example.hello_spring.cv.analysis.scoring.CvScoringEngine;
import com.example.hello_spring.cv.dto.*;
import com.example.hello_spring.cv.exception.*;
import com.example.hello_spring.cv.model.Cv;
import com.example.hello_spring.cv.repository.CvRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CvAnalysisService {

    private final CvRepository cvRepository;
    private final CvAnalysisRepository cvAnalysisRepository;
    private final CvScoringEngine cvScoringEngine;

    public CvAnalysisService(
            CvRepository cvRepository,
            CvAnalysisRepository cvAnalysisRepository,
            CvScoringEngine cvScoringEngine
    ) {
        this.cvRepository = cvRepository;
        this.cvAnalysisRepository = cvAnalysisRepository;
        this.cvScoringEngine = cvScoringEngine;
    }

    // =====================================================
    // PART 1 – Trigger analysis
    // =====================================================
    @Transactional
    public CvAnalysisResponse startAnalysis(
            Long cvId,
            CvAnalysisRequest request,
            String username
    ) {

        Cv cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new CvNotFoundException("CV not found"));

        if (!cv.getOwner().getUsername().equals(username)) {
            throw new CvAccessDeniedException("You do not own this CV");
        }

        CvAnalysis analysis = CvAnalysis.pending(cv);
        CvAnalysis savedAnalysis = cvAnalysisRepository.save(analysis);

        try {
            String cvText = cv.getFilePath();

            var result = cvScoringEngine.analyze(
                    cvText,
                    request != null ? request.getJobDescription() : null
            );

            savedAnalysis.markCompleted(
                    result.getScore(),
                    String.join("\n", result.getStrengths()),
                    String.join("\n", result.getImprovements()),
                    result.getSummary()
            );

        } catch (Exception ex) {
            savedAnalysis.markFailed();
        }

        cvAnalysisRepository.save(savedAnalysis);

        return new CvAnalysisResponse(
                savedAnalysis.getId(),
                savedAnalysis.getStatus(),
                "CV analysis started"
        );
    }

    // =====================================================
    // PART 2 – Get analysis by ID
    // 🔧 PART 5 CHANGE: custom exception
    // =====================================================
    @Transactional(readOnly = true)
    public CvAnalysisResultResponse getAnalysisById(
            Long analysisId,
            String username
    ) {

        CvAnalysis analysis = cvAnalysisRepository.findById(analysisId)
                .orElseThrow(() ->
                        new AnalysisNotFoundException("Analysis not found"));

        if (!analysis.getCv().getOwner().getUsername().equals(username)) {
            throw new CvAccessDeniedException("You do not own this analysis");
        }

        return new CvAnalysisResultResponse(
                analysis.getId(),
                analysis.getStatus(),
                analysis.getScore(),
                analysis.getStrengths(),
                analysis.getWeaknesses(),
                analysis.getSuggestions()
        );
    }

    // =====================================================
    // PART 2 – Latest analysis for CV
    // 🔧 PART 5 CHANGE: clearer empty-history error
    // =====================================================
    @Transactional(readOnly = true)
    public CvAnalysisResultResponse getLatestAnalysisForCv(
            Long cvId,
            String username
    ) {

        Cv cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new CvNotFoundException("CV not found"));

        if (!cv.getOwner().getUsername().equals(username)) {
            throw new CvAccessDeniedException("You do not own this CV");
        }

        CvAnalysis analysis = cvAnalysisRepository
                .findTopByCvIdOrderByCreatedAtDesc(cvId)
                .orElseThrow(() ->
                        new AnalysisNotFoundException(
                                "No analysis found for this CV"
                        ));

        return new CvAnalysisResultResponse(
                analysis.getId(),
                analysis.getStatus(),
                analysis.getScore(),
                analysis.getStrengths(),
                analysis.getWeaknesses(),
                analysis.getSuggestions()
        );
    }

    // =====================================================
    // PART 3 – Retry failed analysis
    // 🔧 PART 5 CHANGE: invalid-state exception
    // =====================================================
    @Transactional
    public CvAnalysisResponse retryAnalysis(
            Long analysisId,
            String username
    ) {

        CvAnalysis failedAnalysis = cvAnalysisRepository.findById(analysisId)
                .orElseThrow(() ->
                        new AnalysisNotFoundException("Analysis not found"));

        if (!failedAnalysis.getCv().getOwner().getUsername().equals(username)) {
            throw new CvAccessDeniedException("You do not own this analysis");
        }

        if (failedAnalysis.getStatus() != AnalysisStatus.FAILED) {
            throw new InvalidAnalysisStateException(
                    "Only failed analyses can be retried"
            );
        }

        Cv cv = failedAnalysis.getCv();
        CvAnalysis newAnalysis = CvAnalysis.pending(cv);
        CvAnalysis savedAnalysis = cvAnalysisRepository.save(newAnalysis);

        try {
            String cvText = cv.getFilePath();

            var result = cvScoringEngine.analyze(cvText, null);

            savedAnalysis.markCompleted(
                    result.getScore(),
                    String.join("\n", result.getStrengths()),
                    String.join("\n", result.getImprovements()),
                    result.getSummary()
            );

        } catch (Exception ex) {
            savedAnalysis.markFailed();
        }

        cvAnalysisRepository.save(savedAnalysis);

        return new CvAnalysisResponse(
                savedAnalysis.getId(),
                savedAnalysis.getStatus(),
                "CV analysis retried"
        );
    }

    // =====================================================
    // PART 4 – History by CV
    // =====================================================
    @Transactional(readOnly = true)
    public List<CvAnalysisHistoryItem> getAnalysisHistoryForCv(
            Long cvId,
            String username
    ) {

        Cv cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new CvNotFoundException("CV not found"));

        if (!cv.getOwner().getUsername().equals(username)) {
            throw new CvAccessDeniedException("You do not own this CV");
        }

        return cvAnalysisRepository
                .findByCvIdOrderByCreatedAtDesc(cvId)
                .stream()
                .map(a -> new CvAnalysisHistoryItem(
                        a.getId(),
                        a.getStatus(),
                        a.getScore(),
                        a.getCreatedAt()
                ))
                .toList();
    }

    // =====================================================
    // PART 4 – History by User
    // =====================================================
    @Transactional(readOnly = true)
    public List<CvAnalysisHistoryItem> getAnalysisHistoryForUser(
            String username
    ) {

        return cvAnalysisRepository
                .findByCvOwnerUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(a -> new CvAnalysisHistoryItem(
                        a.getId(),
                        a.getStatus(),
                        a.getScore(),
                        a.getCreatedAt()
                ))
                .toList();
    }
}
