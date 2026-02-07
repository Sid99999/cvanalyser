package com.example.hello_spring.cv.analysis.service;

import com.example.hello_spring.cv.analysis.model.CvAnalysis;
import com.example.hello_spring.cv.analysis.model.CvAnalysisResult;
import com.example.hello_spring.cv.analysis.scoring.CvScoringEngine;
import com.example.hello_spring.cv.model.Cv;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CvAnalysisService {

    private final CvScoringEngine scoringEngine;

    public CvAnalysisService(CvScoringEngine scoringEngine) {
        this.scoringEngine = scoringEngine;
    }

    @Transactional
    public void analyze(
            CvAnalysis analysis,
            String cvText,
            String jobDescription
    ) {
        try {
            CvAnalysisResult result =
                    scoringEngine.analyze(cvText, jobDescription);

            analysis.markCompleted(
                    result.getScore(),
                    String.join("\n", result.getStrengths()),
                    String.join("\n", result.getImprovements()),
                    result.getSummary()
            );

        } catch (Exception e) {
            analysis.markFailed();
            throw e;
        }
    }

}
