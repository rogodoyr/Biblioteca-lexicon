package com.lexicon.auth.service;

import com.lexicon.auth.dto.LoginRequestDto;
import com.lexicon.auth.dto.TokenResponseDto;
import com.lexicon.auth.exception.AuthenticationException;
import com.lexicon.auth.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public TokenResponseDto login(LoginRequestDto request) {
        log.info("Attempting authentication for user: {}", request.getUsername());
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user: {}", request.getUsername());
            throw new AuthenticationException("Invalid username or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        log.info("Authentication successful for user: {}", request.getUsername());
        return TokenResponseDto.builder()
                .token(token)
                .build();
    }
}

