package com.lexicon.loan.service;

import com.lexicon.loan.dto.LoanRequestDto;
import com.lexicon.loan.dto.LoanResponseDto;
import com.lexicon.loan.entity.Loan;
import com.lexicon.loan.repository.LoanRepository;
import com.lexicon.loan.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    @Transactional(readOnly = true)
    public List<LoanResponseDto> getAllLoans() {
        log.info("Fetching all loans");
        return loanRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoanResponseDto getLoanById(Long id) {
        log.info("Fetching loan with id: {}", id);
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Loan not found with id: {}", id);
                    return new ResourceNotFoundException("Loan not found with id: " + id);
                });
        return mapToResponseDto(loan);
    }

    @Transactional
    public LoanResponseDto createLoan(LoanRequestDto requestDto) {
        log.info("Creating new loan for bookId: {}", requestDto.getBookId());
        Loan loan = Loan.builder()
                .bookId(requestDto.getBookId())
                .userId(requestDto.getUserId())
                .loanDate(requestDto.getLoanDate())
                .returnDate(requestDto.getReturnDate())
                .status(requestDto.getStatus())
                .build();
        Loan savedLoan = loanRepository.save(loan);
        log.info("Loan created successfully with id: {}", savedLoan.getId());
        return mapToResponseDto(savedLoan);
    }

    @Transactional
    public LoanResponseDto updateLoan(Long id, LoanRequestDto requestDto) {
        log.info("Updating loan with id: {}", id);
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Loan not found with id: {}", id);
                    return new ResourceNotFoundException("Loan not found with id: " + id);
                });

        loan.setBookId(requestDto.getBookId());
        loan.setUserId(requestDto.getUserId());
        loan.setLoanDate(requestDto.getLoanDate());
        loan.setReturnDate(requestDto.getReturnDate());
        loan.setStatus(requestDto.getStatus());

        Loan updatedLoan = loanRepository.save(loan);
        log.info("Loan updated successfully with id: {}", updatedLoan.getId());
        return mapToResponseDto(updatedLoan);
    }

    @Transactional
    public void deleteLoan(Long id) {
        log.info("Deleting loan with id: {}", id);
        if (!loanRepository.existsById(id)) {
            log.error("Loan not found with id: {}", id);
            throw new ResourceNotFoundException("Loan not found with id: " + id);
        }
        loanRepository.deleteById(id);
        log.info("Loan deleted successfully with id: {}", id);
    }

    private LoanResponseDto mapToResponseDto(Loan loan) {
        return LoanResponseDto.builder()
                .id(loan.getId())
                .bookId(loan.getBookId())
                .userId(loan.getUserId())
                .loanDate(loan.getLoanDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .build();
    }
}
