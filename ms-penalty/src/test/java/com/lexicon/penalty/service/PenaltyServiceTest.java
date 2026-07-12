package com.lexicon.penalty.service;

import com.lexicon.penalty.dto.PenaltyRequestDto;
import com.lexicon.penalty.dto.PenaltyResponseDto;
import com.lexicon.penalty.entity.Penalty;
import com.lexicon.penalty.repository.PenaltyRepository;
import com.lexicon.penalty.exception.ResourceNotFoundException;
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
public class PenaltyServiceTest {

    @Mock
    private PenaltyRepository penaltyRepository;

    @InjectMocks
    private PenaltyService penaltyService;

    private Penalty penalty;
    private PenaltyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        penalty = Penalty.builder()
                .id(1L)
                .build();

        requestDto = PenaltyRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() {
        when(penaltyRepository.findAll()).thenReturn(List.of(penalty));

        List<PenaltyResponseDto> result = penaltyService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(penaltyRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnPenalty() {
        when(penaltyRepository.findById(1L)).thenReturn(Optional.of(penalty));

        PenaltyResponseDto result = penaltyService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(penaltyRepository, times(1)).findById(1L);
    }

    @Test
    void getById_shouldThrowExceptionWhenNotFound() {
        when(penaltyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> penaltyService.getById(1L));
        verify(penaltyRepository, times(1)).findById(1L);
    }

    @Test
    void create_shouldReturnCreatedPenalty() {
        when(penaltyRepository.save(any(Penalty.class))).thenReturn(penalty);

        PenaltyResponseDto result = penaltyService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(penaltyRepository, times(1)).save(any(Penalty.class));
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(penaltyRepository.existsById(1L)).thenReturn(true);

        penaltyService.delete(1L);

        verify(penaltyRepository, times(1)).existsById(1L);
        verify(penaltyRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowExceptionWhenNotFound() {
        when(penaltyRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> penaltyService.delete(1L));
        verify(penaltyRepository, times(1)).existsById(1L);
        verify(penaltyRepository, never()).deleteById(anyLong());
    }
}
