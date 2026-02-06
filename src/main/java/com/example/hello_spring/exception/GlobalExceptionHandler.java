package com.example.hello_spring.exception;

import com.example.hello_spring.cv.exception.CvAccessDeniedException;
import com.example.hello_spring.cv.exception.CvNotFoundException;
import com.example.hello_spring.cv.exception.InvalidFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =========================
    // 404 – Student not found
    // =========================
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStudentNotFound(StudentNotFoundException ex) {

        log.warn("StudentNotFoundException: {}", ex.getMessage());

        return buildError(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage()
        );
    }

    // =========================
    // 404 – CV not found
    // =========================
    @ExceptionHandler(CvNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCvNotFound(CvNotFoundException ex) {

        log.warn("CvNotFoundException: {}", ex.getMessage());

        return buildError(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage()
        );
    }

    // =========================
    // 403 – CV access denied
    // =========================
    @ExceptionHandler(CvAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleCvAccessDenied(CvAccessDeniedException ex) {

        log.warn("CvAccessDeniedException: {}", ex.getMessage());

        return buildError(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                ex.getMessage()
        );
    }

    // =========================
    // 400 – Invalid CV file upload
    // =========================
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFile(InvalidFileException ex) {

        log.warn("InvalidFileException: {}", ex.getMessage());

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage()
        );
    }

    // =========================
    // 401 – Bad credentials (LOGIN FAILURE)
    // =========================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {

        log.warn("BadCredentialsException: {}", ex.getMessage());

        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Invalid username or password"
        );
    }

    // =========================
    // 400 – Bean validation errors
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        log.warn("Validation failed: {}", fieldErrors);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", Instant.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(error);
    }

    // =========================
    // 409 – Duplicate email
    // =========================
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(DuplicateEmailException ex) {

        log.warn("DuplicateEmailException: {}", ex.getMessage());

        return buildError(
                HttpStatus.CONFLICT,
                "Conflict",
                ex.getMessage()
        );
    }

    // =========================
    // 500 – Unexpected errors (FALLBACK)
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        // Full stack trace only in logs
        log.error("Unhandled exception occurred", ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Something went wrong. Please try again later."
        );
    }

    // =========================
    // Shared error builder
    // =========================
    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status,
            String error,
            String message
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}
