package com.lexicon.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {

    @NotNull
    private Long bookId;
    @NotNull
    private Long userId;
    @NotNull
    private LocalDate reservationDate;
    @NotNull
    private LocalDate expiryDate;
    @NotBlank
    private String status;
}
