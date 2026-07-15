package com.lexicon.auth.exception;

import com.lexicon.auth.glitchtip.GlitchTipErrorReporter;
import com.lexicon.auth.tracing.RequestIdContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final GlitchTipErrorReporter errorReporter;

    public GlobalExceptionHandler(GlitchTipErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        errorReporter.captureException(ex, "Credenciales invalidas");
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        error.put("requestId", RequestIdContext.getOrUnknown());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleApiException(ApiException ex) {
        errorReporter.captureException(ex, "API Exception");
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        error.put("requestId", RequestIdContext.getOrUnknown());
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        errorReporter.captureException(ex, "Excepcion no controlada en la API");
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage() != null ? ex.getMessage() : "Error interno");
        error.put("requestId", RequestIdContext.getOrUnknown());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

