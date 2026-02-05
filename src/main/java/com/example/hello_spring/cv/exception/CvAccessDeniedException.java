package com.example.hello_spring.cv.exception;

public class CvAccessDeniedException extends RuntimeException {
    public CvAccessDeniedException(String message) {
        super(message);
    }
}
