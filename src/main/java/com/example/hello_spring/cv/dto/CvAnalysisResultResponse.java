package com.example.hello_spring.cv.dto;

import com.example.hello_spring.cv.analysis.model.AnalysisStatus;

public class CvAnalysisResultResponse {

    private Long analysisId;
    private AnalysisStatus status;
    private Integer score;
    private String strengths;
    private String weaknesses;
    private String suggestions;

    public CvAnalysisResultResponse(
            Long analysisId,
            AnalysisStatus status,
            Integer score,
            String strengths,
            String weaknesses,
            String suggestions
    ) {
        this.analysisId = analysisId;
        this.status = status;
        this.score = score;
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

    public Integer getScore() {
        return score;
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
