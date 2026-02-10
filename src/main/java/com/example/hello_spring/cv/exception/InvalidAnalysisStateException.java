package com.example.hello_spring.cv.exception;

public class InvalidAnalysisStateException extends RuntimeException {
    public InvalidAnalysisStateException(String message) {
        super(message);
    }
}
