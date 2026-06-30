package com.lexicon.book.service;

import com.lexicon.book.dto.BookRequestDto;
import com.lexicon.book.dto.BookResponseDto;
import com.lexicon.book.entity.Book;
import com.lexicon.book.repository.BookRepository;
import com.lexicon.book.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<BookResponseDto> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        log.info("Fetching book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found with id: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });
        return mapToResponseDto(book);
    }

    @Transactional
    public BookResponseDto createBook(BookRequestDto requestDto) {
        log.info("Creating new book with title: {}", requestDto.getTitle());
        Book book = Book.builder()
                .title(requestDto.getTitle())
                .author(requestDto.getAuthor())
                .isbn(requestDto.getIsbn())
                .build();
        Book savedBook = bookRepository.save(book);
        log.info("Book created successfully with id: {}", savedBook.getId());
        return mapToResponseDto(savedBook);
    }

    @Transactional
    public BookResponseDto updateBook(Long id, BookRequestDto requestDto) {
        log.info("Updating book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found with id: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });

        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());

        Book updatedBook = bookRepository.save(book);
        log.info("Book updated successfully with id: {}", updatedBook.getId());
        return mapToResponseDto(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book with id: {}", id);
        if (!bookRepository.existsById(id)) {
            log.error("Book not found with id: {}", id);
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Book deleted successfully with id: {}", id);
    }

    private BookResponseDto mapToResponseDto(Book book) {
        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .build();
    }
}
