package com.example.hello_spring.cv.analysis.repository;

import com.example.hello_spring.cv.analysis.model.CvAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CvAnalysisRepository extends JpaRepository<CvAnalysis, Long> {

    // Existing (Part 2)
    Optional<CvAnalysis> findTopByCvIdOrderByCreatedAtDesc(Long cvId);

    // ===============================
    // PART 4 – NEW
    // ===============================

    // All analyses for a CV (latest first)
    List<CvAnalysis> findByCvIdOrderByCreatedAtDesc(Long cvId);

    // All analyses for a user (via CV ownership)
    List<CvAnalysis> findByCvOwnerUsernameOrderByCreatedAtDesc(String username);
}
