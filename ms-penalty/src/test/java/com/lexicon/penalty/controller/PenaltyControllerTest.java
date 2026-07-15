package com.lexicon.penalty.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexicon.penalty.dto.PenaltyRequestDto;
import com.lexicon.penalty.dto.PenaltyResponseDto;
import com.lexicon.penalty.exception.ResourceNotFoundException;
import com.lexicon.penalty.glitchtip.GlitchTipErrorReporter;
import com.lexicon.penalty.service.PenaltyService;
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

@WebMvcTest(PenaltyController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PenaltyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PenaltyService penaltyService;

    @MockitoBean
    private GlitchTipErrorReporter glitchTipErrorReporter;

    @Autowired
    private ObjectMapper objectMapper;

    private PenaltyResponseDto responseDto;
    private PenaltyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        responseDto = PenaltyResponseDto.builder()
                .id(1L)
                .build();

        requestDto = PenaltyRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(penaltyService.getAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/penalties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(penaltyService, times(1)).getAll();
    }

    @Test
    void getById_shouldReturnPenalty() throws Exception {
        when(penaltyService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/penalties/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(penaltyService, times(1)).getById(1L);
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(penaltyService.getById(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/penalties/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(penaltyService, times(1)).getById(1L);
    }

    @Test
    void create_shouldReturnCreatedPenalty() throws Exception {
        when(penaltyService.create(any(PenaltyRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/penalties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(penaltyService, times(1)).create(any(PenaltyRequestDto.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(penaltyService).delete(1L);

        mockMvc.perform(delete("/api/v1/penalties/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(penaltyService, times(1)).delete(1L);
    }
}
