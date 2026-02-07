package com.example.hello_spring.ai.dto;

import java.util.List;
import java.util.Map;

public class CvAiAnalysisResponse {

    private int overallScore;
    private Map<String, Integer> sections;
    private List<String> strengths;
    private List<String> improvements;
    private String summary;

    public int getOverallScore() {
        return overallScore;
    }

    public Map<String, Integer> getSections() {
        return sections;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public List<String> getImprovements() {
        return improvements;
    }

    public String getSummary() {
        return summary;
    }
}
