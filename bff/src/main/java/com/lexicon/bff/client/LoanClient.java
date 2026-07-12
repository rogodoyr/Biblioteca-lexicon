package com.lexicon.bff.client;

import com.lexicon.bff.dto.LoanDto;
import com.lexicon.bff.exception.ApiException;
import com.lexicon.bff.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class LoanClient {

    private final WebClient webClient;

    public LoanClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://MS-LOAN/api/v1/loans").build();
    }

    public Mono<LoanDto> getLoanById(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(new ResourceNotFoundException("Loan with id " + id + " not found"));
                    }
                    return Mono.error(new ApiException("Client error while fetching loan", HttpStatus.BAD_REQUEST));
                })
                .onStatus(status -> status.is5xxServerError(), response -> 
                        Mono.error(new ApiException("Server error while fetching loan", HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(LoanDto.class);
    }
}
