package com.lexicon.reservation.service;

import com.lexicon.reservation.dto.ReservationRequestDto;
import com.lexicon.reservation.dto.ReservationResponseDto;
import com.lexicon.reservation.entity.Reservation;
import com.lexicon.reservation.repository.ReservationRepository;
import com.lexicon.reservation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestion de reservas de libros.
 * Proporciona operaciones CRUD completas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * Obtiene todas las reservas del sistema.
     * @return Lista de reservas como DTOs de respuesta
     */
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getAll() {
        log.info("Fetching all reservations");
        return reservationRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una reserva por su ID.
     * @param id Identificador unico de la reserva
     * @return DTO de respuesta con los datos de la reserva
     * @throws ResourceNotFoundException si no se encuentra la reserva
     */
    @Transactional(readOnly = true)
    public ReservationResponseDto getById(Long id) {
        log.info("Fetching reservation with id: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Reservation not found with id: {}", id);
                    return new ResourceNotFoundException("Reservation not found with id: " + id);
                });
        return mapToResponseDto(reservation);
    }

    /**
     * Crea una nueva reserva en el sistema.
     * @param requestDto DTO con los datos de la reserva a crear
     * @return DTO de respuesta con la reserva creada y su ID generado
     */
    @Transactional
    public ReservationResponseDto create(ReservationRequestDto requestDto) {
        log.info("Creating new reservation");
        Reservation reservation = Reservation.builder()
                .bookId(requestDto.getBookId())
                .userId(requestDto.getUserId())
                .reservationDate(requestDto.getReservationDate())
                .expiryDate(requestDto.getExpiryDate())
                .status(requestDto.getStatus())
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation created successfully with id: {}", savedReservation.getId());
        return mapToResponseDto(savedReservation);
    }

    /**
     * Actualiza una reserva existente.
     * @param id Identificador de la reserva a actualizar
     * @param requestDto DTO con los nuevos datos de la reserva
     * @return DTO de respuesta con la reserva actualizada
     * @throws ResourceNotFoundException si no se encuentra la reserva
     */
    @Transactional
    public ReservationResponseDto update(Long id, ReservationRequestDto requestDto) {
        log.info("Updating reservation with id: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Reservation not found with id: {}", id);
                    return new ResourceNotFoundException("Reservation not found with id: " + id);
                });

        reservation.setBookId(requestDto.getBookId());
        reservation.setUserId(requestDto.getUserId());
        reservation.setReservationDate(requestDto.getReservationDate());
        reservation.setExpiryDate(requestDto.getExpiryDate());
        reservation.setStatus(requestDto.getStatus());
        Reservation updatedReservation = reservationRepository.save(reservation);
        log.info("Reservation updated successfully with id: {}", updatedReservation.getId());
        return mapToResponseDto(updatedReservation);
    }

    /**
     * Elimina una reserva del sistema.
     * @param id Identificador de la reserva a eliminar
     * @throws ResourceNotFoundException si no se encuentra la reserva
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting reservation with id: {}", id);
        if (!reservationRepository.existsById(id)) {
            log.error("Reservation not found with id: {}", id);
            throw new ResourceNotFoundException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
        log.info("Reservation deleted successfully with id: {}", id);
    }

    private ReservationResponseDto mapToResponseDto(Reservation reservation) {
        return ReservationResponseDto.builder()
                .id(reservation.getId())
                .bookId(reservation.getBookId())
                .userId(reservation.getUserId())
                .reservationDate(reservation.getReservationDate())
                .expiryDate(reservation.getExpiryDate())
                .status(reservation.getStatus())
                .build();
    }
}
