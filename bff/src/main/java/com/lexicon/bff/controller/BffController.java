package com.lexicon.bff.controller;

import com.lexicon.bff.dto.ConsolidatedLoanDto;
import com.lexicon.bff.service.BffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bff")
@RequiredArgsConstructor
@Tag(name = "BFF API", description = "Backend For Frontend APIs to orchestrate microservices")
public class BffController {

    private final BffService bffService;

    @GetMapping("/loans/{id}")
    @Operation(summary = "Get consolidated loan details", description = "Retrieves loan details along with the associated book details")
    public Mono<ResponseEntity<ConsolidatedLoanDto>> getConsolidatedLoan(@PathVariable Long id) {
        return bffService.getConsolidatedLoan(id)
                .map(ResponseEntity::ok);
    }
}
