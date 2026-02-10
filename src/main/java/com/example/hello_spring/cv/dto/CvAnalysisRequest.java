package com.example.hello_spring.cv.dto;

public class CvAnalysisRequest {

    // Optional for now, but critical for future job-matching
    private String jobDescription;

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
