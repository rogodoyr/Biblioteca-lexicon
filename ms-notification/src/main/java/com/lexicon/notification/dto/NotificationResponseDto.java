package com.lexicon.notification.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;

    private Long userId;
    private String type;
    private String message;
    private Boolean readStatus;
    private LocalDateTime createdAt;
}
