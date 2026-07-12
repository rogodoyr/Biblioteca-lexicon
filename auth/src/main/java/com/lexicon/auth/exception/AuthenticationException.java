package com.lexicon.auth.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends ApiException {
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
