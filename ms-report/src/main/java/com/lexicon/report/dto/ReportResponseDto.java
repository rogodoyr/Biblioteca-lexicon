package com.lexicon.report.dto;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {
    private Long id;

    private String reportType;
    private Long generatedBy;
    
    private String parameters;
    
    private String result;
    private LocalDateTime createdAt;
}
