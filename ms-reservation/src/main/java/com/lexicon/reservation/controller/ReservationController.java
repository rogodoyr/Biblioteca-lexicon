package com.lexicon.reservation.controller;

import com.lexicon.reservation.dto.ReservationRequestDto;
import com.lexicon.reservation.dto.ReservationResponseDto;
import com.lexicon.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation API", description = "CRUD operations for Reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "Get all reservations", description = "Retrieves a list of all reservations")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> getAll() {
        return ResponseEntity.ok(reservationService.getAll());
    }

    @Operation(summary = "Get a reservation by ID", description = "Retrieves a specific reservation by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation found"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @Operation(summary = "Create a new reservation", description = "Creates a new reservation record")
    @ApiResponse(responseCode = "201", description = "Reservation created successfully")
    @PostMapping
    public ResponseEntity<ReservationResponseDto> create(@RequestBody ReservationRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(requestDto));
    }

    @Operation(summary = "Update an existing reservation", description = "Updates the details of an existing reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> update(@PathVariable Long id, @RequestBody ReservationRequestDto requestDto) {
        return ResponseEntity.ok(reservationService.update(id, requestDto));
    }

    @Operation(summary = "Delete a reservation", description = "Deletes a reservation by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reservation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
