package com.lexicon.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {
    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private String status;
}
