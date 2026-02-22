package com.example.hello_spring.ai.parser;

import com.example.hello_spring.ai.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class CvAiResponseParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static CvAiAnalysisResponse parse(String json) {

        try {
            CvAiAnalysisResponse raw =
                    mapper.readValue(json, CvAiAnalysisResponse.class);

            return sanitize(raw);

        } catch (Exception e) {
            throw new RuntimeException("Invalid AI response format", e);
        }
    }

    private static CvAiAnalysisResponse sanitize(CvAiAnalysisResponse raw) {

        if (raw.getSectionScores() == null) {
            throw new IllegalArgumentException("AI response missing sectionScores");
        }

        SectionScores rawScores = raw.getSectionScores();

        SectionScores sanitizedScores = new SectionScores(
                clamp(rawScores.getExperienceScore()),
                clamp(rawScores.getProjectsScore()),
                clamp(rawScores.getSkillsScore()),
                clamp(rawScores.getEducationScore()),
                clamp(rawScores.getSummaryScore())
        );

        List<AdditionalSection> additionalSections =
                safeList(raw.getAdditionalSections());

        List<String> missingKeywords =
                safeList(raw.getMissingKeywords());

        List<String> formattingRedFlags =
                safeList(raw.getFormattingRedFlags());

        List<String> strengths =
                safeList(raw.getStrengths());

        List<String> weaknesses =
                safeList(raw.getWeaknesses());

        List<String> suggestions =
                safeList(raw.getSuggestions());

        return new CvAiAnalysisResponse(
                sanitizedScores,
                additionalSections,
                missingKeywords,
                formattingRedFlags,
                strengths,
                weaknesses,
                suggestions
        );
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private static <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
