package com.lexicon.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {
    @NotNull(message = "El ID del libro es obligatorio")
    private Long bookId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "La fecha de préstamo es obligatoria")
    private LocalDate loanDate;

    @NotNull(message = "La fecha de devolución es obligatoria")
    private LocalDate returnDate;

    @NotBlank(message = "El estado del préstamo es obligatorio")
    private String status;
}
