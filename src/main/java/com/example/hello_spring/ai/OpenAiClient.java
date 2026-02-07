package com.example.hello_spring.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class OpenAiClient {

    private static final String OPENAI_URL =
            "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    public OpenAiClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String call(String prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system",
                                "content", "You are a helpful assistant."),
                        Map.of("role", "user",
                                "content", prompt)
                ),
                "temperature", 0.2
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        OPENAI_URL,
                        entity,
                        String.class
                );

        return extractText(response.getBody());
    }

    private String extractText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }
    }
}

