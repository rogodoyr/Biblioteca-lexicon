package com.lexicon.auth.service;

import com.lexicon.auth.dto.LoginRequestDto;
import com.lexicon.auth.dto.TokenResponseDto;
import com.lexicon.auth.exception.AuthenticationException;
import com.lexicon.auth.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDto loginRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDto("testuser", "password123");
        userDetails = User.withUsername("testuser")
                .password("ignored")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void login_Success_ReturnsToken() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // AuthenticationManager returns Authentication, but we don't use it
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mocked.jwt.token");

        // When
        TokenResponseDto response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void login_BadCredentials_ThrowsAuthenticationException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When / Then
        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));
        verify(jwtService, never()).generateToken(any());
    }
}

