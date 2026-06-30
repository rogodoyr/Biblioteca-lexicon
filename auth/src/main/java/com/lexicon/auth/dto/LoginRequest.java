package com.lexicon.auth.dto;

// Request payload expected by POST /login.
public record LoginRequest(String username, String password) {
}
