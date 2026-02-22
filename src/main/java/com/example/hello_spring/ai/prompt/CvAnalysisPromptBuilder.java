package com.example.hello_spring.ai.prompt;

public class CvAnalysisPromptBuilder {

    private static final int MAX_PROMPT_LENGTH = 15000;

    public static String build(String cvText, String jobDescription) {

        String safeCvText = sanitize(cvText);
        String safeJobDescription = sanitize(jobDescription);

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
You are a senior technical recruiter evaluating resumes in a highly competitive tech job market.

Your evaluation must be strict, realistic, and conservative.

SCORING PHILOSOPHY:
- Most resumes should score between 55 and 75.
- Only exceptional resumes should exceed 85.
- Very weak resumes should fall below 50.
- Do NOT inflate scores.
- Penalize vague statements.
- Penalize lack of measurable impact.
- Penalize irrelevant experience.
- Reward quantified results and strong technical depth.

SUGGESTION RULES:
- Suggestions must be specific and actionable.
- Explain WHAT to improve and HOW.
- Mention which section it improves.
- Focus only on changes that would increase the score.
- Avoid generic advice.

IMPORTANT:
Return ONLY valid JSON.
Do NOT include markdown.
Do NOT include explanations.
Do NOT include extra commentary.
Do NOT wrap the JSON in backticks.

The response MUST strictly follow this exact JSON schema:

{
  "sectionScores": {
    "experienceScore": number,
    "skillsScore": number,
    "projectsScore": number,
    "educationScore": number,
    "summaryScore": number
  },
  "additionalSections": [
    {
      "name": string,
      "score": number,
      "comment": string
    }
  ],
  "missingKeywords": [string],
  "formattingRedFlags": [string],
  "strengths": [string],
  "weaknesses": [string],
  "suggestions": [string]
}

Rules:
- All scores must be integers.
- If a section does not exist, assign a realistic low score.
- Do not invent experience.
- Do not hallucinate technologies not present in the CV.
""");

        if (safeJobDescription != null && !safeJobDescription.isBlank()) {
            prompt.append("\n\nJob Description:\n")
                    .append(safeJobDescription);
        }

        prompt.append("\n\nCV Content:\n")
                .append(safeCvText);

        String finalPrompt = prompt.toString();

        // Prompt length safety cap
        if (finalPrompt.length() > MAX_PROMPT_LENGTH) {
            finalPrompt = finalPrompt.substring(0, MAX_PROMPT_LENGTH);
        }

        return finalPrompt;
    }

    // =====================================================
    // Basic Prompt Injection Sanitizer
    // =====================================================

    private static String sanitize(String input) {

        if (input == null) return null;

        String cleaned = input;

        // Remove role-switch attempts
        cleaned = cleaned.replaceAll("(?i)assistant:", "");
        cleaned = cleaned.replaceAll("(?i)system:", "");
        cleaned = cleaned.replaceAll("(?i)user:", "");

        // Remove instruction override attempts
        cleaned = cleaned.replaceAll("(?i)ignore previous instructions", "");
        cleaned = cleaned.replaceAll("(?i)disregard above", "");
        cleaned = cleaned.replaceAll("(?i)override system", "");
        cleaned = cleaned.replaceAll("(?i)act as", "");

        return cleaned.trim();
    }
}