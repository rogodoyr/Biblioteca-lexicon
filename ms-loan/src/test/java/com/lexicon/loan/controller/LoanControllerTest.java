package com.lexicon.loan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lexicon.loan.dto.LoanRequestDto;
import com.lexicon.loan.dto.LoanResponseDto;
import com.lexicon.loan.exception.ResourceNotFoundException;
import com.lexicon.loan.service.LoanService;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoanResponseDto responseDto;
    private LoanRequestDto requestDto;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        
        responseDto = LoanResponseDto.builder()
                .id(1L)
                .bookId(10L)
                .userId(20L)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(7))
                .status("ACTIVE")
                .build();

        requestDto = LoanRequestDto.builder()
                .bookId(10L)
                .userId(20L)
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(7))
                .status("ACTIVE")
                .build();
    }

    @Test
    void getAllLoans_shouldReturnList() throws Exception {
        when(loanService.getAllLoans()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(10L))
                .andExpect(jsonPath("$.size()").value(1));

        verify(loanService, times(1)).getAllLoans();
    }

    @Test
    void getLoanById_shouldReturnLoan() throws Exception {
        when(loanService.getLoanById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/loans/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(10L));

        verify(loanService, times(1)).getLoanById(1L);
    }

    @Test
    void getLoanById_shouldReturn404WhenNotFound() throws Exception {
        when(loanService.getLoanById(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/loans/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(loanService, times(1)).getLoanById(1L);
    }

    @Test
    void createLoan_shouldReturnCreatedLoan() throws Exception {
        when(loanService.createLoan(any(LoanRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(loanService, times(1)).createLoan(any(LoanRequestDto.class));
    }

    @Test
    void updateLoan_shouldReturnUpdatedLoan() throws Exception {
        when(loanService.updateLoan(eq(1L), any(LoanRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/loans/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(loanService, times(1)).updateLoan(eq(1L), any(LoanRequestDto.class));
    }

    @Test
    void deleteLoan_shouldReturnNoContent() throws Exception {
        doNothing().when(loanService).deleteLoan(1L);

        mockMvc.perform(delete("/api/v1/loans/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(loanService, times(1)).deleteLoan(1L);
    }
}
