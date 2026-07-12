package com.lexicon.loan.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends ApiException {
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
