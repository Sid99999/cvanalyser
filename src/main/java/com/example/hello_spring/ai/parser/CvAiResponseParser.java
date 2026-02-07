package com.example.hello_spring.ai.parser;

import com.example.hello_spring.ai.dto.CvAiAnalysisResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CvAiResponseParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static CvAiAnalysisResponse parse(String json) {
        try {
            return mapper.readValue(json, CvAiAnalysisResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid AI response format", e);
        }
    }
}
