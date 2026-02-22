package com.example.hello_spring.exception;

import com.example.hello_spring.cv.exception.RateLimitExceededException;
import com.example.hello_spring.cv.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =====================================================
    // 404 – NOT FOUND
    // =====================================================

    @ExceptionHandler({
            StudentNotFoundException.class,
            CvNotFoundException.class,
            AnalysisNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFound(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        log.warn("NotFound: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // =====================================================
    // 403 – FORBIDDEN
    // =====================================================

    @ExceptionHandler(CvAccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            CvAccessDeniedException ex,
            HttpServletRequest request
    ) {
        log.warn("AccessDenied: {}", ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
    }

    // =====================================================
    // 409 – CONFLICT
    // =====================================================

    @ExceptionHandler({
            InvalidAnalysisStateException.class,
            DuplicateEmailException.class
    })
    public ResponseEntity<ApiError> handleConflict(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    // =====================================================
    // 400 – BAD REQUEST
    // =====================================================

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiError> handleInvalidFile(
            InvalidFileException ex,
            HttpServletRequest request
    ) {
        log.warn("InvalidFile: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    // =====================================================
    // 401 – UNAUTHORIZED
    // =====================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        log.warn("BadCredentials attempt");
        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                request,
                null
        );
    }

    // =====================================================
    // 429 – RATE LIMIT EXCEEDED
    // =====================================================

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiError> handleRateLimitExceeded(
            RateLimitExceededException ex,
            HttpServletRequest request
    ) {
        log.warn("RateLimitExceeded: {}", ex.getMessage());

        return buildError(
                HttpStatus.TOO_MANY_REQUESTS,
                ex.getMessage(),
                request,
                null
        );
    }

    // =====================================================
    // 400 – BEAN VALIDATION
    // =====================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(err ->
                        fieldErrors.put(
                                err.getField(),
                                err.getDefaultMessage()
                        )
                );

        log.warn("Validation failed: {}", fieldErrors);

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                fieldErrors
        );
    }

    // =====================================================
    // 500 – INTERNAL SERVER ERROR
    // =====================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception", ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later.",
                request,
                null
        );
    }

    // =====================================================
    // SHARED ERROR BUILDER
    // =====================================================

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors
    ) {

        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(status).body(error);
    }
}
