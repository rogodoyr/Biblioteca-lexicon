package com.lexicon.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexicon.book.dto.BookRequestDto;
import com.lexicon.book.dto.BookResponseDto;
import com.lexicon.book.exception.ResourceNotFoundException;
import com.lexicon.book.glitchtip.GlitchTipErrorReporter;
import com.lexicon.book.service.BookService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private GlitchTipErrorReporter glitchTipErrorReporter;

    @Autowired
    private ObjectMapper objectMapper;

    private BookResponseDto responseDto;
    private BookRequestDto requestDto;

    @BeforeEach
    void setUp() {
        responseDto = BookResponseDto.builder()
                .id(1L)
                .title("Spring Boot Guide")
                .author("John Doe")
                .isbn("1234567890")
                .build();

        requestDto = BookRequestDto.builder()
                .title("Spring Boot Guide")
                .author("John Doe")
                .isbn("1234567890")
                .build();
    }

    @Test
    void getAllBooks_shouldReturnList() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Boot Guide"))
                .andExpect(jsonPath("$.size()").value(1));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void getBookById_shouldReturnBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot Guide"));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void getBookById_shouldReturn404WhenNotFound() throws Exception {
        when(bookService.getBookById(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/books/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void createBook_shouldReturnCreatedBook() throws Exception {
        when(bookService.createBook(any(BookRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookService, times(1)).createBook(any(BookRequestDto.class));
    }

    @Test
    void updateBook_shouldReturnUpdatedBook() throws Exception {
        when(bookService.updateBook(eq(1L), any(BookRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookService, times(1)).updateBook(eq(1L), any(BookRequestDto.class));
    }

    @Test
    void deleteBook_shouldReturnNoContent() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/v1/books/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }
}
