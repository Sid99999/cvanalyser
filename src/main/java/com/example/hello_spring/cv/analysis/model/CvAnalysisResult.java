package com.example.hello_spring.cv.analysis.model;

import java.util.List;

public class CvAnalysisResult {

    private final int score;
    private final List<String> strengths;
    private final List<String> improvements;
    private final String summary;

    public CvAnalysisResult(
            int score,
            List<String> strengths,
            List<String> improvements,
            String summary
    ) {
        this.score = score;
        this.strengths = strengths;
        this.improvements = improvements;
        this.summary = summary;
    }

    public int getScore() {
        return score;
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
