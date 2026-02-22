package com.example.hello_spring.cv.analysis.scoring;

import com.example.hello_spring.ai.dto.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("rule")
public class RuleBasedCvScoringEngine implements CvScoringEngine {

    @Override
    public CvAiAnalysisResponse analyze(String cvText, String jobDescription) {

        SectionScores scores = new SectionScores(
                60, // experience
                55, // projects
                65, // skills
                70, // education
                50  // summary
        );

        return new CvAiAnalysisResponse(
                scores,
                List.of(),
                List.of(),
                List.of(),
                List.of("Add more technical keywords"),
                List.of("No measurable achievements"),
                List.of("Add quantified results")
        );
    }
}
