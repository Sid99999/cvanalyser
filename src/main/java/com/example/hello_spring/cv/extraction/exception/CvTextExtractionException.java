package com.example.hello_spring.cv.extraction.exception;

public class CvTextExtractionException extends RuntimeException {

    public CvTextExtractionException(String message) {
        super(message);
    }

    public CvTextExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
