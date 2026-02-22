package com.example.hello_spring.cv.analysis.service;

import com.example.hello_spring.ai.dto.AdditionalSection;
import com.example.hello_spring.ai.dto.CvAiAnalysisResponse;
import com.example.hello_spring.ai.dto.SectionScores;
import com.example.hello_spring.cv.analysis.model.*;
import com.example.hello_spring.cv.analysis.repository.CvAnalysisRepository;
import com.example.hello_spring.cv.analysis.scoring.CvScoringEngine;
import com.example.hello_spring.cv.dto.*;
import com.example.hello_spring.cv.exception.*;
import com.example.hello_spring.cv.model.Cv;
import com.example.hello_spring.cv.repository.CvRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CvAnalysisService {

    private static final Logger log =
            LoggerFactory.getLogger(CvAnalysisService.class);

    private final CvRepository cvRepository;
    private final CvAnalysisRepository cvAnalysisRepository;
    private final CvScoringEngine cvScoringEngine;
    private final RateLimitService rateLimitService;

    public CvAnalysisService(
            CvRepository cvRepository,
            CvAnalysisRepository cvAnalysisRepository,
            CvScoringEngine cvScoringEngine,
            RateLimitService rateLimitService
    ) {
        this.cvRepository = cvRepository;
        this.cvAnalysisRepository = cvAnalysisRepository;
        this.cvScoringEngine = cvScoringEngine;
        this.rateLimitService = rateLimitService;
    }

    // =====================================================
    // START ANALYSIS
    // =====================================================

    @Transactional
    public CvAnalysisResponse startAnalysis(
            Long cvId,
            CvAnalysisRequest request,
            String username
    ) {

        rateLimitService.checkAnalysisLimit(username);

        log.info("User '{}' starting analysis for CV id={}", username, cvId);

        Cv cv = validateCvOwnership(cvId, username);

        if (cv.getExtractedText() == null || cv.getExtractedText().isBlank()) {
            throw new InvalidAnalysisStateException("CV text has not been extracted yet");
        }

        CvAnalysis analysis = CvAnalysis.pending(cv);
        CvAnalysis saved = cvAnalysisRepository.save(analysis);

        try {
            CvAiAnalysisResponse aiResponse =
                    cvScoringEngine.analyze(
                            cv.getExtractedText(),
                            request != null ? request.getJobDescription() : null
                    );

            applyScoring(saved, aiResponse);

            log.info("Analysis completed for CV id={} with final score={}",
                    cvId, saved.getFinalScore());

        } catch (Exception e) {
            log.error("Analysis failed for CV id={}", cvId, e);
            saved.markFailed();
        }

        cvAnalysisRepository.save(saved);

        return new CvAnalysisResponse(
                saved.getId(),
                saved.getStatus(),
                "CV analysis started"
        );
    }


    // =====================================================
    // RETRY ANALYSIS
    // =====================================================

    @Transactional
    public CvAnalysisResponse retryAnalysis(Long analysisId, String username) {

        log.info("User '{}' retrying analysis id={}", username, analysisId);

        CvAnalysis failed = validateAnalysisOwnership(analysisId, username);

        if (failed.getStatus() != AnalysisStatus.FAILED) {
            throw new InvalidAnalysisStateException("Only FAILED analyses can be retried");
        }

        Cv cv = failed.getCv();

        if (cv.getExtractedText() == null || cv.getExtractedText().isBlank()) {
            throw new InvalidAnalysisStateException("CV text has not been extracted yet");
        }

        CvAnalysis newAnalysis = CvAnalysis.pending(cv);
        CvAnalysis saved = cvAnalysisRepository.save(newAnalysis);

        try {
            CvAiAnalysisResponse aiResponse =
                    cvScoringEngine.analyze(cv.getExtractedText(), null);

            applyScoring(saved, aiResponse);

            log.info("Retry analysis completed for analysisId={} final score={}",
                    saved.getId(), saved.getFinalScore());

        } catch (Exception e) {
            log.error("Retry analysis failed for analysisId={}", analysisId, e);
            saved.markFailed();
        }

        cvAnalysisRepository.save(saved);

        return new CvAnalysisResponse(
                saved.getId(),
                saved.getStatus(),
                "CV analysis retried"
        );
    }

    // =====================================================
    // GET ANALYSIS BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public CvAnalysisResultResponse getAnalysisById(
            Long analysisId,
            String username
    ) {

        CvAnalysis analysis =
                validateAnalysisOwnership(analysisId, username);

        return mapToResultResponse(analysis);
    }

    // =====================================================
    // GET LATEST ANALYSIS FOR CV
    // =====================================================

    @Transactional(readOnly = true)
    public CvAnalysisResultResponse getLatestAnalysisForCv(
            Long cvId,
            String username
    ) {

        validateCvOwnership(cvId, username);

        CvAnalysis analysis = cvAnalysisRepository
                .findTopByCvIdOrderByCreatedAtDesc(cvId)
                .orElseThrow(() ->
                        new AnalysisNotFoundException("No analysis found")
                );

        return mapToResultResponse(analysis);
    }

    // =====================================================
    // HISTORY BY CV
    // =====================================================

    @Transactional(readOnly = true)
    public List<CvAnalysisHistoryItem> getAnalysisHistoryForCv(
            Long cvId,
            String username
    ) {

        validateCvOwnership(cvId, username);

        return cvAnalysisRepository
                .findByCvIdOrderByCreatedAtDesc(cvId)
                .stream()
                .map(a -> new CvAnalysisHistoryItem(
                        a.getId(),
                        a.getStatus(),
                        a.getFinalScore(),
                        a.getCreatedAt()
                ))
                .toList();
    }

    // =====================================================
    // HISTORY BY USER
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
                        a.getFinalScore(),
                        a.getCreatedAt()
                ))
                .toList();
    }

    // =====================================================
    // STRICT SCORING + JD ALIGNMENT
    // =====================================================

    private void applyScoring(
            CvAnalysis analysis,
            CvAiAnalysisResponse aiResponse
    ) {

        SectionScores dtoScores = aiResponse.getSectionScores();

        int baseScore = calculateBaseScore(dtoScores);
        int bonusScore = calculateBonusScore(aiResponse.getAdditionalSections());
        int penaltyScore = calculatePenaltyScore(aiResponse);
        int alignmentAdjustment = calculateAlignmentScore(aiResponse);

        // 🔹 Step 1: Raw score calculation
        int rawScore = baseScore
                + bonusScore
                - penaltyScore
                + alignmentAdjustment;

        // 🔹 Step 2: Apply normalization curve
        int finalScore = normalizeScore(rawScore);

        // 🔹 Step 3: Safety floor (avoid catastrophic collapse)
        finalScore = Math.max(35, finalScore);

        // 🔹 Step 4: Elite ceiling (avoid inflated perfection)
        finalScore = Math.min(92, finalScore);

        analysis.markCompleted(
                baseScore,
                bonusScore,
                finalScore,
                toEmbeddable(dtoScores),
                toEmbeddableList(aiResponse.getAdditionalSections()),
                aiResponse.getMissingKeywords(),
                aiResponse.getFormattingRedFlags(),
                join(aiResponse.getStrengths()),
                join(aiResponse.getWeaknesses()),
                join(aiResponse.getSuggestions())
        );
    }

    private int calculateBaseScore(SectionScores scores) {

        double weighted =
                0.45 * scores.getExperienceScore() +
                        0.25 * scores.getProjectsScore() +
                        0.15 * scores.getSkillsScore() +
                        0.10 * scores.getEducationScore() +
                        0.05 * scores.getSummaryScore();

        return (int) Math.round(weighted);
    }

    private int calculateBonusScore(List<AdditionalSection> sections) {

        if (sections == null || sections.isEmpty()) return 0;

        int bonus = 0;

        for (AdditionalSection s : sections) {

            String name = s.getName().toUpperCase();
            int score = s.getScore();

            if (isBonusEligible(name)) {
                if (score > 85) bonus += 2;
                else if (score >= 75) bonus += 1;
            }
        }

        return Math.min(bonus, 3);
    }

    private boolean isBonusEligible(String name) {
        return name.equals("PUBLICATIONS")
                || name.equals("RESEARCH")
                || name.equals("CERTIFICATIONS")
                || name.equals("PATENTS");
    }

    private int calculatePenaltyScore(CvAiAnalysisResponse aiResponse) {

        int penalty = 0;

        // 🔴 Missing keyword penalty (JD alignment)
        if (aiResponse.getMissingKeywords() != null) {

            int missing = aiResponse.getMissingKeywords().size();

            if (missing >= 10) penalty += 6;
            else if (missing >= 7) penalty += 5;
            else if (missing >= 4) penalty += 3;
            else if (missing >= 2) penalty += 1;
        }

        // 🔴 Formatting penalty (softer cap)
        if (aiResponse.getFormattingRedFlags() != null) {
            penalty += Math.min(4,
                    aiResponse.getFormattingRedFlags().size());
        }

        // 🔴 Weakness realism penalty (capped)
        if (aiResponse.getWeaknesses() != null
                && !aiResponse.getWeaknesses().isEmpty()) {

            String combined = String
                    .join(" ", aiResponse.getWeaknesses())
                    .toLowerCase();

            int weaknessPenalty = 0;

            if (combined.contains("vague")) weaknessPenalty += 3;
            if (combined.contains("no measurable")) weaknessPenalty += 4;
            if (combined.contains("lack of experience")) weaknessPenalty += 5;
            if (combined.contains("irrelevant")) weaknessPenalty += 3;
            if (combined.contains("generic")) weaknessPenalty += 2;

            // Cap weakness impact
            penalty += Math.min(7, weaknessPenalty);
        }

        return penalty;
    }
    private int calculateAlignmentScore(CvAiAnalysisResponse aiResponse) {

        if (aiResponse.getMissingKeywords() == null
                || aiResponse.getMissingKeywords().isEmpty()) {
            return 0; // No JD → no alignment bonus
        }

        int missing = aiResponse.getMissingKeywords().size();

        if (missing <= 2) return 3;
        if (missing <= 4) return 1;
        if (missing <= 7) return 0;
        return -6;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private SectionScoresEmbeddable toEmbeddable(SectionScores dto) {

        SectionScoresEmbeddable emb = new SectionScoresEmbeddable();
        emb.setExperienceScore(dto.getExperienceScore());
        emb.setProjectsScore(dto.getProjectsScore());
        emb.setSkillsScore(dto.getSkillsScore());
        emb.setEducationScore(dto.getEducationScore());
        emb.setSummaryScore(dto.getSummaryScore());
        return emb;
    }

    private List<AdditionalSectionEmbeddable> toEmbeddableList(
            List<AdditionalSection> dtoList
    ) {

        if (dtoList == null || dtoList.isEmpty()) return List.of();

        return dtoList.stream()
                .map(dto -> {
                    AdditionalSectionEmbeddable emb =
                            new AdditionalSectionEmbeddable();
                    emb.setName(dto.getName());
                    emb.setScore(dto.getScore());
                    emb.setComment(dto.getComment());
                    return emb;
                })
                .toList();
    }

    private String join(List<String> list) {
        return (list == null || list.isEmpty())
                ? ""
                : String.join("\n", list);
    }

    private Cv validateCvOwnership(Long cvId, String username) {

        Cv cv = cvRepository.findById(cvId)
                .orElseThrow(() ->
                        new CvNotFoundException("CV not found"));

        if (!cv.getOwner().getUsername().equals(username)) {
            throw new CvAccessDeniedException("You do not own this CV");
        }

        return cv;
    }

    private CvAnalysis validateAnalysisOwnership(
            Long analysisId,
            String username
    ) {

        CvAnalysis analysis = cvAnalysisRepository.findById(analysisId)
                .orElseThrow(() ->
                        new AnalysisNotFoundException("Analysis not found"));

        if (!analysis.getCv()
                .getOwner()
                .getUsername()
                .equals(username)) {

            throw new CvAccessDeniedException(
                    "You do not own this analysis"
            );
        }

        return analysis;
    }

    private CvAnalysisResultResponse mapToResultResponse(
            CvAnalysis analysis
    ) {

        return new CvAnalysisResultResponse(
                analysis.getId(),
                analysis.getStatus(),
                analysis.getFinalScore(),
                analysis.getSectionScores(),
                analysis.getAdditionalSections(),
                analysis.getMissingKeywords(),
                analysis.getFormattingRedFlags(),
                analysis.getStrengths(),
                analysis.getWeaknesses(),
                analysis.getSuggestions()
        );
    }

    private int normalizeScore(int rawScore) {

        // Clamp first
        rawScore = Math.max(0, rawScore);
        rawScore = Math.min(100, rawScore);

        // Apply dampening curve
        if (rawScore > 85) {
            return 85 + (rawScore - 85) / 3;
        }

        if (rawScore < 50) {
            return 50 - (50 - rawScore) / 2;
        }

        return rawScore;
    }
}