package com.example.hello_spring.cv.analysis.model;

public class AdditionalSectionEmbeddable {

    private String name;
    private int score;
    private String comment;

    public AdditionalSectionEmbeddable() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
