package com.example.hello_spring.cv.analysis.model;

import com.example.hello_spring.cv.model.Cv;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "cv_analyses")
public class CvAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id", nullable = false, unique = true)
    private Cv cv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Column
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected CvAnalysis() {}

    private CvAnalysis(Cv cv) {
        this.cv = cv;
        this.status = AnalysisStatus.PENDING;
    }

    public static CvAnalysis pending(Cv cv) {
        return new CvAnalysis(cv);
    }

    public void markCompleted(
            int score,
            String strengths,
            String weaknesses,
            String suggestions
    ) {
        this.status = AnalysisStatus.COMPLETED;
        this.score = score;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.suggestions = suggestions;
    }

    public void markFailed() {
        this.status = AnalysisStatus.FAILED;
    }

    // getters only (immutability discipline)
    public Long getId() { return id; }
    public Cv getCv() { return cv; }
    public AnalysisStatus getStatus() { return status; }
    public Integer getScore() { return score; }
    public String getStrengths() { return strengths; }
    public String getWeaknesses() { return weaknesses; }
    public String getSuggestions() { return suggestions; }
    public Instant getCreatedAt() { return createdAt; }
}
