package com.example.hello_spring.cv.analysis.scoring;

import com.example.hello_spring.cv.analysis.model.CvAnalysisResult;

public interface CvScoringEngine {

    CvAnalysisResult analyze(String cvText, String jobDescription);
}
