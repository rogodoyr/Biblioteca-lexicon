package com.lexicon.penalty.controller;

import com.lexicon.penalty.dto.PenaltyRequestDto;
import com.lexicon.penalty.dto.PenaltyResponseDto;
import com.lexicon.penalty.service.PenaltyService;
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
@RequestMapping("/api/v1/penalties")
@RequiredArgsConstructor
@Tag(name = "Penalty API", description = "CRUD operations for Penaltys")
public class PenaltyController {

    private final PenaltyService penaltyService;

    @Operation(summary = "Get all penalties", description = "Retrieves a list of all penalties")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    public ResponseEntity<List<PenaltyResponseDto>> getAll() {
        return ResponseEntity.ok(penaltyService.getAll());
    }

    @Operation(summary = "Get a penalty by ID", description = "Retrieves a specific penalty by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Penalty found"),
            @ApiResponse(responseCode = "404", description = "Penalty not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PenaltyResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(penaltyService.getById(id));
    }

    @Operation(summary = "Create a new penalty", description = "Creates a new penalty record")
    @ApiResponse(responseCode = "201", description = "Penalty created successfully")
    @PostMapping
    public ResponseEntity<PenaltyResponseDto> create(@RequestBody PenaltyRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(penaltyService.create(requestDto));
    }

    @Operation(summary = "Update an existing penalty", description = "Updates the details of an existing penalty")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Penalty updated successfully"),
            @ApiResponse(responseCode = "404", description = "Penalty not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PenaltyResponseDto> update(@PathVariable Long id, @RequestBody PenaltyRequestDto requestDto) {
        return ResponseEntity.ok(penaltyService.update(id, requestDto));
    }

    @Operation(summary = "Delete a penalty", description = "Deletes a penalty by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Penalty deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Penalty not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        penaltyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
