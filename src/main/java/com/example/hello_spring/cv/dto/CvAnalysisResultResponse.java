package com.example.hello_spring.cv.dto;

import com.example.hello_spring.cv.analysis.model.*;

import java.util.List;

public class CvAnalysisResultResponse {

    private Long analysisId;
    private AnalysisStatus status;
    private Integer finalScore;

    private SectionScoresEmbeddable sectionScores;
    private List<AdditionalSectionEmbeddable> additionalSections;
    private List<String> missingKeywords;
    private List<String> formattingRedFlags;

    private String strengths;
    private String weaknesses;
    private String suggestions;

    public CvAnalysisResultResponse(
            Long analysisId,
            AnalysisStatus status,
            Integer finalScore,
            SectionScoresEmbeddable sectionScores,
            List<AdditionalSectionEmbeddable> additionalSections,
            List<String> missingKeywords,
            List<String> formattingRedFlags,
            String strengths,
            String weaknesses,
            String suggestions
    ) {
        this.analysisId = analysisId;
        this.status = status;
        this.finalScore = finalScore;
        this.sectionScores = sectionScores;
        this.additionalSections = additionalSections;
        this.missingKeywords = missingKeywords;
        this.formattingRedFlags = formattingRedFlags;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.suggestions = suggestions;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public Integer getFinalScore() {
        return finalScore;
    }

    public SectionScoresEmbeddable getSectionScores() {
        return sectionScores;
    }

    public List<AdditionalSectionEmbeddable> getAdditionalSections() {
        return additionalSections;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public List<String> getFormattingRedFlags() {
        return formattingRedFlags;
    }

    public String getStrengths() {
        return strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public String getSuggestions() {
        return suggestions;
    }
}
