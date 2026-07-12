package com.lexicon.notification.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends ApiException {
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
