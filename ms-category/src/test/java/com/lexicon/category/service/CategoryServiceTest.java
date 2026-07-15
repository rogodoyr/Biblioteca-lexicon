package com.lexicon.category.service;

import com.lexicon.category.dto.CategoryRequestDto;
import com.lexicon.category.dto.CategoryResponseDto;
import com.lexicon.category.entity.Category;
import com.lexicon.category.repository.CategoryRepository;
import com.lexicon.category.exception.ResourceNotFoundException;
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
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequestDto requestDto;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .build();

        requestDto = CategoryRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryResponseDto> result = categoryService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponseDto result = categoryService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getById_shouldThrowExceptionWhenNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getById(1L));
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void create_shouldReturnCreatedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDto result = categoryService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowExceptionWhenNotFound() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(1L));
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
