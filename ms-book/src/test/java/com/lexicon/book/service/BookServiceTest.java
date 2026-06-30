package com.lexicon.book.service;

import com.lexicon.book.dto.BookRequestDto;
import com.lexicon.book.dto.BookResponseDto;
import com.lexicon.book.entity.Book;
import com.lexicon.book.repository.BookRepository;
import com.lexicon.book.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookRequestDto requestDto;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Test Title")
                .author("Test Author")
                .isbn("1234567890")
                .build();

        requestDto = BookRequestDto.builder()
                .title("Test Title")
                .author("Test Author")
                .isbn("1234567890")
                .build();
    }

    @Test
    void getAllBooks_shouldReturnList() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookResponseDto> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_shouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponseDto result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookById_shouldThrowExceptionWhenNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(1L));
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void createBook_shouldReturnCreatedBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponseDto result = bookService.createBook(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_shouldReturnUpdatedBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponseDto result = bookService.updateBook(1L, requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_shouldThrowExceptionWhenNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(1L, requestDto));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_shouldDeleteSuccessfully() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBook_shouldThrowExceptionWhenNotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, never()).deleteById(anyLong());
    }
}
