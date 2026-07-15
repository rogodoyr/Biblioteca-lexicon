package com.lexicon.penalty.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyRequestDto {

    @NotNull
    private Long loanId;
    @NotNull
    private Long userId;
    @NotNull
    private Double amount;
    @NotBlank
    private String reason;
    @NotBlank
    private String status;
    @NotNull
    private LocalDateTime createdAt;
}
