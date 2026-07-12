package com.lexicon.loan.controller;

import com.lexicon.loan.dto.LoanRequestDto;
import com.lexicon.loan.dto.LoanResponseDto;
import com.lexicon.loan.service.LoanService;
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
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loan API", description = "CRUD operations for Loans")
public class LoanController {

    private final LoanService loanService;

    @Operation(summary = "Get all loans", description = "Retrieves a list of all loans in the system")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @Operation(summary = "Get a loan by ID", description = "Retrieves a specific loan by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan found"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @Operation(summary = "Create a new loan", description = "Creates a new loan record")
    @ApiResponse(responseCode = "201", description = "Loan created successfully")
    @PostMapping
    public ResponseEntity<LoanResponseDto> createLoan(@RequestBody LoanRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(requestDto));
    }

    @Operation(summary = "Update an existing loan", description = "Updates the details of an existing loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan updated successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LoanResponseDto> updateLoan(@PathVariable Long id, @RequestBody LoanRequestDto requestDto) {
        return ResponseEntity.ok(loanService.updateLoan(id, requestDto));
    }

    @Operation(summary = "Delete a loan", description = "Deletes a loan by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
