package com.lexicon.notification.dto;
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
public class NotificationRequestDto {

    @NotNull
    private Long userId;
    @NotBlank
    private String type;
    @NotBlank
    private String message;
    @NotNull
    private Boolean readStatus;
    @NotNull
    private LocalDateTime createdAt;
}
