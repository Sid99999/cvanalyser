package com.example.hello_spring.cv.dto;

import com.example.hello_spring.cv.analysis.model.AnalysisStatus;

import java.time.LocalDateTime;

public class CvAnalysisHistoryItem {

    private Long analysisId;
    private AnalysisStatus status;
    private Integer finalScore;
    private LocalDateTime createdAt;

    public CvAnalysisHistoryItem(
            Long analysisId,
            AnalysisStatus status,
            Integer finalScore,
            LocalDateTime createdAt
    ) {
        this.analysisId = analysisId;
        this.status = status;
        this.finalScore = finalScore;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
