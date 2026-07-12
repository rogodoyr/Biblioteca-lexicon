package com.lexicon.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {
    private Long id;

    private Long bookId;
    private Long userId;
    private LocalDate reservationDate;
    private LocalDate expiryDate;
    private String status;
}
