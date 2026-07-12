package com.lexicon.bff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidatedLoanDto {
    private Long loanId;
    private Long userId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private String loanStatus;
    private BookDto book;
}
