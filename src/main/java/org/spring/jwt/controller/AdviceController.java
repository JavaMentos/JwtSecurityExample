package org.spring.jwt.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.lang.Strings;
import org.spring.jwt.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.SignatureException;
import java.util.Optional;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class})
    public ResponseEntity<ErrorResponse> handleJwtException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "JWT Error: " + ex.getMessage(),
                System.currentTimeMillis()
                );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = extractReadableMessage(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), message, System.currentTimeMillis()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Optional<FieldError> fieldError = Optional.ofNullable(ex.getBindingResult().getFieldError());
        String message = fieldError.isPresent() ? fieldError.get().getDefaultMessage() : Strings.EMPTY;

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error: " + message,
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private String extractReadableMessage(DataIntegrityViolationException ex) {
        String detailedMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        if (detailedMessage.contains("users_username_key")) {
            return "Username already exists.";
        }
        return "Database error: " + detailedMessage;
    }
}
