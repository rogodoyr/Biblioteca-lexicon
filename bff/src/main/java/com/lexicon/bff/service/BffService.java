package com.lexicon.bff.service;

import com.lexicon.bff.client.BookClient;
import com.lexicon.bff.client.LoanClient;
import com.lexicon.bff.dto.BookDto;
import com.lexicon.bff.dto.ConsolidatedLoanDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BffService {

    private final LoanClient loanClient;
    private final BookClient bookClient;

    public Mono<ConsolidatedLoanDto> getConsolidatedLoan(Long loanId) {
        log.info("Fetching consolidated loan for id: {}", loanId);
        
        return loanClient.getLoanById(loanId)
                .flatMap(loan -> {
                    log.info("Loan found. Fetching book details for bookId: {}", loan.getBookId());
                    return bookClient.getBookById(loan.getBookId())
                            // If book is not found or fails, we can either return empty book or fail the whole request.
                            // Here we return a fallback BookDto if we want the loan to still show up.
                            // But since the requirements don't specify, we'll let the error propagate if book is 404,
                            // unless we want to catch it. Let's handle errors gracefully by providing an empty book on error.
                            .onErrorResume(e -> {
                                log.error("Failed to fetch book details for bookId {}: {}", loan.getBookId(), e.getMessage());
                                BookDto fallbackBook = BookDto.builder()
                                        .id(loan.getBookId())
                                        .title("Unknown/Unavailable Book")
                                        .author("Unknown")
                                        .isbn("Unknown")
                                        .build();
                                return Mono.just(fallbackBook);
                            })
                            .map(book -> ConsolidatedLoanDto.builder()
                                    .loanId(loan.getId())
                                    .userId(loan.getUserId())
                                    .loanDate(loan.getLoanDate())
                                    .returnDate(loan.getReturnDate())
                                    .loanStatus(loan.getStatus())
                                    .book(book)
                                    .build());
                });
    }
}
