package com.example.hello_spring.cv.exception;

public class AnalysisNotFoundException extends RuntimeException {
    public AnalysisNotFoundException(String message) {
        super(message);
    }
}
