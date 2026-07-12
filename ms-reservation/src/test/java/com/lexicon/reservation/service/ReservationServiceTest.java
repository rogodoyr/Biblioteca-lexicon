package com.lexicon.reservation.service;

import com.lexicon.reservation.dto.ReservationRequestDto;
import com.lexicon.reservation.dto.ReservationResponseDto;
import com.lexicon.reservation.entity.Reservation;
import com.lexicon.reservation.repository.ReservationRepository;
import com.lexicon.reservation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
    private ReservationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        reservation = Reservation.builder()
                .id(1L)
                .build();

        requestDto = ReservationRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<ReservationResponseDto> result = reservationService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        ReservationResponseDto result = reservationService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void getById_shouldThrowExceptionWhenNotFound() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.getById(1L));
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void create_shouldReturnCreatedReservation() {
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponseDto result = reservationService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(reservationRepository.existsById(1L)).thenReturn(true);

        reservationService.delete(1L);

        verify(reservationRepository, times(1)).existsById(1L);
        verify(reservationRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowExceptionWhenNotFound() {
        when(reservationRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> reservationService.delete(1L));
        verify(reservationRepository, times(1)).existsById(1L);
        verify(reservationRepository, never()).deleteById(anyLong());
    }
}
