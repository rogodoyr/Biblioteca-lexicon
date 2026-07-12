package com.lexicon.loan.service;

import com.lexicon.loan.dto.LoanRequestDto;
import com.lexicon.loan.dto.LoanResponseDto;
import com.lexicon.loan.entity.Loan;
import com.lexicon.loan.repository.LoanRepository;
import com.lexicon.loan.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan loan;
    private LoanRequestDto requestDto;

    @BeforeEach
    void setUp() {
        loan = Loan.builder()
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
    void getAllLoans_shouldReturnList() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));

        List<LoanResponseDto> result = loanService.getAllLoans();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getBookId());
        verify(loanRepository, times(1)).findAll();
    }

    @Test
    void getLoanById_shouldReturnLoan() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        LoanResponseDto result = loanService.getLoanById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10L, result.getBookId());
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    void getLoanById_shouldThrowExceptionWhenNotFound() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.getLoanById(1L));
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    void createLoan_shouldReturnCreatedLoan() {
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LoanResponseDto result = loanService.createLoan(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10L, result.getBookId());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void updateLoan_shouldReturnUpdatedLoan() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LoanResponseDto result = loanService.updateLoan(1L, requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(loanRepository, times(1)).findById(1L);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void updateLoan_shouldThrowExceptionWhenNotFound() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.updateLoan(1L, requestDto));
        verify(loanRepository, times(1)).findById(1L);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void deleteLoan_shouldDeleteSuccessfully() {
        when(loanRepository.existsById(1L)).thenReturn(true);

        loanService.deleteLoan(1L);

        verify(loanRepository, times(1)).existsById(1L);
        verify(loanRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteLoan_shouldThrowExceptionWhenNotFound() {
        when(loanRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> loanService.deleteLoan(1L));
        verify(loanRepository, times(1)).existsById(1L);
        verify(loanRepository, never()).deleteById(anyLong());
    }
}
