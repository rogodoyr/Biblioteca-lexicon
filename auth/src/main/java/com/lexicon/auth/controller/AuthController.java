package com.lexicon.auth.controller;

import com.lexicon.auth.dto.LoginRequestDto;
import com.lexicon.auth.dto.TokenResponseDto;
import com.lexicon.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@jakarta.validation.Valid @RequestBody LoginRequestDto request) {
        log.info("Received login request for user: {}", request.getUsername());
        TokenResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
