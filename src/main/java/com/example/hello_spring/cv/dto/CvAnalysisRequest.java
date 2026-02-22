package com.example.hello_spring.cv.dto;

import jakarta.validation.constraints.Size;

public class CvAnalysisRequest {

    // Optional JD but length-limited
    @Size(
            max = 5000,
            message = "Job description cannot exceed 5000 characters"
    )
    private String jobDescription;

    public String getJobDescription() {
        if (jobDescription == null) return null;

        String trimmed = jobDescription.trim();

        return trimmed.isBlank() ? null : trimmed;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
