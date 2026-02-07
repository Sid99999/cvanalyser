package com.example.hello_spring.ai.prompt;

public class CvAnalysisPromptBuilder {

    public static String build(String cvText, String jobDescription) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
        You are a professional technical recruiter and CV evaluator.

        Analyze the following CV and return ONLY valid JSON.
        Do NOT include markdown.
        Do NOT include explanations.
        Do NOT include extra text.

        JSON schema:
        {
          "overallScore": number (0-100),
          "sections": {
            "skills": number,
            "experience": number,
            "education": number,
            "projects": number
          },
          "strengths": [string],
          "improvements": [string],
          "summary": string
        }

        Rules:
        - Scores must be integers
        - Be strict and realistic
        - Penalize vague content
        - Reward quantified impact
        """);

        if (jobDescription != null && !jobDescription.isBlank()) {
            prompt.append("""
            
            Job Description:
            """).append(jobDescription);
        }

        prompt.append("""
        
        CV Content:
        """).append(cvText);

        return prompt.toString();
    }
}
