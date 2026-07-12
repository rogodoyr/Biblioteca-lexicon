package com.lexicon.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexicon.notification.dto.NotificationRequestDto;
import com.lexicon.notification.dto.NotificationResponseDto;
import com.lexicon.notification.exception.ResourceNotFoundException;
import com.lexicon.notification.glitchtip.GlitchTipErrorReporter;
import com.lexicon.notification.service.NotificationService;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private GlitchTipErrorReporter glitchTipErrorReporter;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationResponseDto responseDto;
    private NotificationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        responseDto = NotificationResponseDto.builder()
                .id(1L)
                .build();

        requestDto = NotificationRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(notificationService.getAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(notificationService, times(1)).getAll();
    }

    @Test
    void getById_shouldReturnNotification() throws Exception {
        when(notificationService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/notifications/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(notificationService, times(1)).getById(1L);
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(notificationService.getById(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/notifications/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getById(1L);
    }

    @Test
    void create_shouldReturnCreatedNotification() throws Exception {
        when(notificationService.create(any(NotificationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(notificationService, times(1)).create(any(NotificationRequestDto.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(notificationService).delete(1L);

        mockMvc.perform(delete("/api/v1/notifications/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).delete(1L);
    }
}
