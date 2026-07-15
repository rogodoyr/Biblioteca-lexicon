package com.lexicon.penalty.dto;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyResponseDto {
    private Long id;

    private Long loanId;
    private Long userId;
    
    private Double amount;
    
    private String reason;
    private String status;
    private LocalDateTime createdAt;
}
