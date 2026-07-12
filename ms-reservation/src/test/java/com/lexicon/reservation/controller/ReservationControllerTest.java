package com.lexicon.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexicon.reservation.dto.ReservationRequestDto;
import com.lexicon.reservation.dto.ReservationResponseDto;
import com.lexicon.reservation.exception.ResourceNotFoundException;
import com.lexicon.reservation.glitchtip.GlitchTipErrorReporter;
import com.lexicon.reservation.service.ReservationService;
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

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private GlitchTipErrorReporter glitchTipErrorReporter;

    @Autowired
    private ObjectMapper objectMapper;

    private ReservationResponseDto responseDto;
    private ReservationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        responseDto = ReservationResponseDto.builder()
                .id(1L)
                .build();

        requestDto = ReservationRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(reservationService.getAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(reservationService, times(1)).getAll();
    }

    @Test
    void getById_shouldReturnReservation() throws Exception {
        when(reservationService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/reservations/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(reservationService, times(1)).getById(1L);
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(reservationService.getById(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/reservations/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(reservationService, times(1)).getById(1L);
    }

    @Test
    void create_shouldReturnCreatedReservation() throws Exception {
        when(reservationService.create(any(ReservationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(reservationService, times(1)).create(any(ReservationRequestDto.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(reservationService).delete(1L);

        mockMvc.perform(delete("/api/v1/reservations/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).delete(1L);
    }
}
