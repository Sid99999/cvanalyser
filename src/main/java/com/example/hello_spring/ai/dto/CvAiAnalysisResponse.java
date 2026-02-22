package com.example.hello_spring.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public class CvAiAnalysisResponse {

    private SectionScores sectionScores;
    private List<AdditionalSection> additionalSections;
    private List<String> missingKeywords;
    private List<String> formattingRedFlags;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> suggestions;

    // ✅ Required for Jackson
    public CvAiAnalysisResponse() {
    }

    // Used by our sanitizer / rule engine
    public CvAiAnalysisResponse(
            SectionScores sectionScores,
            List<AdditionalSection> additionalSections,
            List<String> missingKeywords,
            List<String> formattingRedFlags,
            List<String> strengths,
            List<String> weaknesses,
            List<String> suggestions
    ) {
        this.sectionScores = sectionScores;
        this.additionalSections = additionalSections;
        this.missingKeywords = missingKeywords;
        this.formattingRedFlags = formattingRedFlags;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.suggestions = suggestions;
    }

    public SectionScores getSectionScores() {
        return sectionScores;
    }

    public List<AdditionalSection> getAdditionalSections() {
        return additionalSections;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public List<String> getFormattingRedFlags() {
        return formattingRedFlags;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }
}
