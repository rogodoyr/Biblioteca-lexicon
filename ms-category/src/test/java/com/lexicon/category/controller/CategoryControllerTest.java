package com.lexicon.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexicon.category.dto.CategoryRequestDto;
import com.lexicon.category.dto.CategoryResponseDto;
import com.lexicon.category.exception.ResourceNotFoundException;
import com.lexicon.category.glitchtip.GlitchTipErrorReporter;
import com.lexicon.category.service.CategoryService;
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

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private GlitchTipErrorReporter glitchTipErrorReporter;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryResponseDto responseDto;
    private CategoryRequestDto requestDto;

    @BeforeEach
    void setUp() {
        responseDto = CategoryResponseDto.builder()
                .id(1L)
                .build();

        requestDto = CategoryRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(categoryService.getAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(categoryService, times(1)).getAll();
    }

    @Test
    void getById_shouldReturnCategory() throws Exception {
        when(categoryService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(categoryService, times(1)).getById(1L);
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(categoryService.getById(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).getById(1L);
    }

    @Test
    void create_shouldReturnCreatedCategory() throws Exception {
        when(categoryService.create(any(CategoryRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(categoryService, times(1)).create(any(CategoryRequestDto.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/api/v1/categories/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).delete(1L);
    }
}
