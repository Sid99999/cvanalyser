package com.example.hello_spring.cv.analysis.scoring;

import com.example.hello_spring.ai.dto.CvAiAnalysisResponse;


public interface CvScoringEngine {

    CvAiAnalysisResponse analyze(String cvText, String jobDescription);

}
