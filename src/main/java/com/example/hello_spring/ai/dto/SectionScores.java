package com.example.hello_spring.ai.dto;

public class SectionScores {

    private int experienceScore;
    private int projectsScore;
    private int skillsScore;
    private int educationScore;
    private int summaryScore;

    public SectionScores() {
        // Needed for Jackson
    }

    public SectionScores(
            int experienceScore,
            int projectsScore,
            int skillsScore,
            int educationScore,
            int summaryScore
    ) {
        this.experienceScore = experienceScore;
        this.projectsScore = projectsScore;
        this.skillsScore = skillsScore;
        this.educationScore = educationScore;
        this.summaryScore = summaryScore;
    }

    public int getExperienceScore() {
        return experienceScore;
    }

    public int getProjectsScore() {
        return projectsScore;
    }

    public int getSkillsScore() {
        return skillsScore;
    }

    public int getEducationScore() {
        return educationScore;
    }

    public int getSummaryScore() {
        return summaryScore;
    }
}
