package com.lexicon.report.dto;
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
public class ReportRequestDto {

    @NotBlank
    private String reportType;
    @NotNull
    private Long generatedBy;
    
    private String parameters;
    
    private String result;
    @NotNull
    private LocalDateTime createdAt;
}
