package com.example.hello_spring.cv.analysis.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class SectionScoresEmbeddable {

    private int experienceScore;
    private int projectsScore;
    private int skillsScore;
    private int educationScore;
    private int summaryScore;

    public int getExperienceScore() { return experienceScore; }
    public void setExperienceScore(int experienceScore) { this.experienceScore = experienceScore; }

    public int getProjectsScore() { return projectsScore; }
    public void setProjectsScore(int projectsScore) { this.projectsScore = projectsScore; }

    public int getSkillsScore() { return skillsScore; }
    public void setSkillsScore(int skillsScore) { this.skillsScore = skillsScore; }

    public int getEducationScore() { return educationScore; }
    public void setEducationScore(int educationScore) { this.educationScore = educationScore; }

    public int getSummaryScore() { return summaryScore; }
    public void setSummaryScore(int summaryScore) { this.summaryScore = summaryScore; }
}
