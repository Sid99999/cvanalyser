package com.example.hello_spring.cv.dto;

import com.example.hello_spring.cv.analysis.model.AnalysisStatus;

import java.time.Instant;

public class CvAnalysisHistoryItem {

    private Long analysisId;
    private AnalysisStatus status;
    private Integer score;
    private Instant createdAt;

    public CvAnalysisHistoryItem(
            Long analysisId,
            AnalysisStatus status,
            Integer score,
            Instant createdAt
    ) {
        this.analysisId = analysisId;
        this.status = status;
        this.score = score;
        this.createdAt = createdAt;
    }

    public Long getAnalysisId() { return analysisId; }
    public AnalysisStatus getStatus() { return status; }
    public Integer getScore() { return score; }
    public Instant getCreatedAt() { return createdAt; }
}

