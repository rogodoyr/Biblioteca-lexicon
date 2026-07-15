package com.lexicon.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexicon.auth.dto.LoginRequestDto;
import com.lexicon.auth.dto.TokenResponseDto;
import com.lexicon.auth.exception.GlobalExceptionHandler;
import com.lexicon.auth.exception.InvalidCredentialsException;
import com.lexicon.auth.glitchtip.GlitchTipErrorReporter;
import com.lexicon.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private GlitchTipErrorReporter glitchTipErrorReporter;

    @Test
    void login_Success() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "password");
        TokenResponseDto response = new TokenResponseDto("valid.token");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid.token"));
    }

    @Test
    void login_Failure_ThrowsInvalidCredentialsException() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "wrong");

        when(authService.login(any(LoginRequestDto.class))).thenThrow(new InvalidCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void login_Failure_ValidationException() throws Exception {
        LoginRequestDto request = new LoginRequestDto("", ""); // Invalid empty fields

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("El nombre de usuario es obligatorio"))
                .andExpect(jsonPath("$.password").value("La contraseña no puede estar en blanco"));
    }
}
