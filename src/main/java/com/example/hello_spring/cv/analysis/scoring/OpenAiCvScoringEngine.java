package com.example.hello_spring.cv.analysis.scoring;

import com.example.hello_spring.ai.OpenAiClient;
import com.example.hello_spring.ai.dto.CvAiAnalysisResponse;
import com.example.hello_spring.ai.parser.CvAiResponseParser;
import com.example.hello_spring.ai.prompt.CvAnalysisPromptBuilder;
import com.example.hello_spring.cv.analysis.model.CvAnalysisResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("ai")
public class OpenAiCvScoringEngine implements CvScoringEngine {

    private final OpenAiClient openAiClient;

    public OpenAiCvScoringEngine(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public CvAnalysisResult analyze(String cvText, String jobDescription) {

        String prompt =
                CvAnalysisPromptBuilder.build(cvText, jobDescription);

        String aiRawResponse = openAiClient.call(prompt);

        CvAiAnalysisResponse parsed =
                CvAiResponseParser.parse(aiRawResponse);

        return new CvAnalysisResult(
                parsed.getOverallScore(),
                parsed.getStrengths(),
                parsed.getImprovements(),
                parsed.getSummary()
        );
    }
}