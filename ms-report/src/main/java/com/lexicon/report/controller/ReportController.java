package com.lexicon.report.controller;

import com.lexicon.report.dto.ReportRequestDto;
import com.lexicon.report.dto.ReportResponseDto;
import com.lexicon.report.service.ReportService;
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
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Report API", description = "CRUD operations for Reports")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get all reports", description = "Retrieves a list of all reports")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    public ResponseEntity<List<ReportResponseDto>> getAll() {
        return ResponseEntity.ok(reportService.getAll());
    }

    @Operation(summary = "Get a report by ID", description = "Retrieves a specific report by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report found"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getById(id));
    }

    @Operation(summary = "Create a new report", description = "Creates a new report record")
    @ApiResponse(responseCode = "201", description = "Report created successfully")
    @PostMapping
    public ResponseEntity<ReportResponseDto> create(@RequestBody ReportRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.create(requestDto));
    }

    @Operation(summary = "Update an existing report", description = "Updates the details of an existing report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report updated successfully"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReportResponseDto> update(@PathVariable Long id, @RequestBody ReportRequestDto requestDto) {
        return ResponseEntity.ok(reportService.update(id, requestDto));
    }

    @Operation(summary = "Delete a report", description = "Deletes a report by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Report deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
