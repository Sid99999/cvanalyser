package com.example.hello_spring.cv.analysis.scoring;

import com.example.hello_spring.cv.analysis.model.CvAnalysisResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("rule")
public class RuleBasedCvScoringEngine implements CvScoringEngine {

    @Override
    public CvAnalysisResult analyze(String cvText, String jobDescription) {

        int score = cvText.length() > 1000 ? 70 : 55;

        return new CvAnalysisResult(
                score,
                List.of("Has core sections"),
                List.of("Lacks depth in projects"),
                "Expand project explanations"
        );
    }
}
