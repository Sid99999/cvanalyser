package com.example.hello_spring.cv.analysis.scoring;

import com.example.hello_spring.ai.OpenAiClient;
import com.example.hello_spring.ai.dto.CvAiAnalysisResponse;
import com.example.hello_spring.ai.parser.CvAiResponseParser;
import com.example.hello_spring.ai.prompt.CvAnalysisPromptBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class OpenAiCvScoringEngine implements CvScoringEngine {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiCvScoringEngine.class);

    private final OpenAiClient openAiClient;

    public OpenAiCvScoringEngine(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public CvAiAnalysisResponse analyze(String cvText, String jobDescription) {

        log.info("AI scoring started");

        try {
            String prompt = CvAnalysisPromptBuilder.build(cvText, jobDescription);

            String aiRawResponse = openAiClient.call(prompt);

            log.debug("AI raw response received (length={})",
                    aiRawResponse != null ? aiRawResponse.length() : 0);

            CvAiAnalysisResponse parsed =
                    CvAiResponseParser.parse(aiRawResponse);

            log.info("AI scoring completed successfully");

            return parsed;

        } catch (Exception e) {
            log.error("AI scoring failed", e);
            throw e;
        }
    }
}