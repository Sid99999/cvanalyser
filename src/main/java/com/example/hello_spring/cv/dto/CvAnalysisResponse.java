package com.example.hello_spring.cv.dto;

import com.example.hello_spring.cv.analysis.model.AnalysisStatus;

public class CvAnalysisResponse {

    private Long analysisId;
    private AnalysisStatus status;
    private String message;

    public CvAnalysisResponse(Long analysisId, AnalysisStatus status, String message) {
        this.analysisId = analysisId;
        this.status = status;
        this.message = message;
    }

    public Long getAnalysisId() {
        return analysisId;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
