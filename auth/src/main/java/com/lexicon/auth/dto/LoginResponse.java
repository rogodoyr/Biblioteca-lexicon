package com.lexicon.auth.dto;

// Response payload returned when credentials are valid.
public record LoginResponse(String token, String tokenType, long expiresInSeconds) {
}
