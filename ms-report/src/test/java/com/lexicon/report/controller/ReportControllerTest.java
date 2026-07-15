package com.lexicon.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexicon.report.dto.ReportRequestDto;
import com.lexicon.report.dto.ReportResponseDto;
import com.lexicon.report.exception.ResourceNotFoundException;
import com.lexicon.report.glitchtip.GlitchTipErrorReporter;
import com.lexicon.report.service.ReportService;
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

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private GlitchTipErrorReporter glitchTipErrorReporter;

    @Autowired
    private ObjectMapper objectMapper;

    private ReportResponseDto responseDto;
    private ReportRequestDto requestDto;

    @BeforeEach
    void setUp() {
        responseDto = ReportResponseDto.builder()
                .id(1L)
                .build();

        requestDto = ReportRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(reportService.getAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(reportService, times(1)).getAll();
    }

    @Test
    void getById_shouldReturnReport() throws Exception {
        when(reportService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/reports/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(reportService, times(1)).getById(1L);
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(reportService.getById(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/reports/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(reportService, times(1)).getById(1L);
    }

    @Test
    void create_shouldReturnCreatedReport() throws Exception {
        when(reportService.create(any(ReportRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(reportService, times(1)).create(any(ReportRequestDto.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(reportService).delete(1L);

        mockMvc.perform(delete("/api/v1/reports/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(reportService, times(1)).delete(1L);
    }
}
