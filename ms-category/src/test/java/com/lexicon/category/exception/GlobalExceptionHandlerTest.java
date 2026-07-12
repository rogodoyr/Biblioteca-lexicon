package com.lexicon.category.exception;
import static org.mockito.Mockito.mock;
import com.lexicon.category.glitchtip.GlitchTipErrorReporter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler(mock(com.lexicon.category.glitchtip.GlitchTipErrorReporter.class));
    }

    @Test
    void handleApiException_shouldReturnResponseEntity() {
        ApiException ex = new ApiException("Custom API error", HttpStatus.BAD_REQUEST);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleApiException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Custom API error", response.getBody().message());
        assertEquals(400, response.getBody().status());
    }

    @Test
    void handleGenericException_shouldReturn500() {
        Exception ex = new Exception("Generic error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Generic error", response.getBody().message());
        assertEquals(500, response.getBody().status());
    }
}
