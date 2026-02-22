package com.example.hello_spring.cv.analysis.model;

import com.example.hello_spring.cv.model.Cv;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cv_analysis")
public class CvAnalysis {

    // =====================================================
    // Core Identity
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cv cv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // =====================================================
    // Deterministic Scoring
    // =====================================================

    @Column(nullable = false)
    private int baseScore;

    @Column(nullable = false)
    private int bonusScore;

    @Column(nullable = false)
    private int finalScore;

    // =====================================================
    // Structured Section Scores (Core Sections)
    // =====================================================

    @Embedded
    private SectionScoresEmbeddable sectionScores;

    // =====================================================
    // Additional Sections (Dynamic - JSONB)
    // =====================================================

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<AdditionalSectionEmbeddable> additionalSections;

    // =====================================================
    // ATS Metadata (JSONB)
    // =====================================================

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> missingKeywords;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> formattingRedFlags;

    // =====================================================
    // Human-Readable Feedback
    // =====================================================

    @Column(length = 5000)
    private String strengths;

    @Column(length = 5000)
    private String weaknesses;

    @Column(length = 5000)
    private String suggestions;

    // =====================================================
    // Constructors
    // =====================================================

    protected CvAnalysis() {
        // JPA only
    }

    private CvAnalysis(Cv cv) {
        this.cv = cv;
        this.status = AnalysisStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.baseScore = 0;
        this.bonusScore = 0;
        this.finalScore = 0;
    }

    // =====================================================
    // Factory Method
    // =====================================================

    public static CvAnalysis pending(Cv cv) {
        return new CvAnalysis(cv);
    }

    // =====================================================
    // Lifecycle Transitions
    // =====================================================

    public void markCompleted(
            int baseScore,
            int bonusScore,
            int finalScore,
            SectionScoresEmbeddable sectionScores,
            List<AdditionalSectionEmbeddable> additionalSections,
            List<String> missingKeywords,
            List<String> formattingRedFlags,
            String strengths,
            String weaknesses,
            String suggestions
    ) {
        this.baseScore = baseScore;
        this.bonusScore = bonusScore;
        this.finalScore = finalScore;
        this.sectionScores = sectionScores;
        this.additionalSections = additionalSections;
        this.missingKeywords = missingKeywords;
        this.formattingRedFlags = formattingRedFlags;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.suggestions = suggestions;
        this.status = AnalysisStatus.COMPLETED;
    }

    public void markFailed() {
        this.status = AnalysisStatus.FAILED;
        this.finalScore = 0;
    }

    // =====================================================
    // Getters (No public setters – controlled mutation)
    // =====================================================

    public Long getId() { return id; }

    public Cv getCv() { return cv; }

    public AnalysisStatus getStatus() { return status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public int getBaseScore() { return baseScore; }

    public int getBonusScore() { return bonusScore; }

    public int getFinalScore() { return finalScore; }

    public SectionScoresEmbeddable getSectionScores() { return sectionScores; }

    public List<AdditionalSectionEmbeddable> getAdditionalSections() { return additionalSections; }

    public List<String> getMissingKeywords() { return missingKeywords; }

    public List<String> getFormattingRedFlags() { return formattingRedFlags; }

    public String getStrengths() { return strengths; }

    public String getWeaknesses() { return weaknesses; }

    public String getSuggestions() { return suggestions; }
}
