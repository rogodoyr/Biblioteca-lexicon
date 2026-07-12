package com.lexicon.bff.exception;

import com.lexicon.bff.glitchtip.GlitchTipErrorReporter;
import com.lexicon.bff.tracing.RequestIdContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final GlitchTipErrorReporter errorReporter;

    public GlobalExceptionHandler(GlitchTipErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        errorReporter.captureException(ex, "API Exception");
        ErrorResponse error = new ErrorResponse(ex.getMessage(), ex.getStatus().value(), LocalDateTime.now());
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        errorReporter.captureException(ex, "Excepcion no controlada en la API");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", ex.getMessage() != null ? ex.getMessage() : "Error interno",
                "requestId", RequestIdContext.getOrUnknown()));
    }
}