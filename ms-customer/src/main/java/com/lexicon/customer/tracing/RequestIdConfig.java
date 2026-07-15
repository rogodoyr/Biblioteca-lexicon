package com.lexicon.customer.tracing;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestIdConfig {

    @Bean
    RestClientCustomizer requestIdRestClientCustomizer() {
        return builder -> builder.requestInterceptor((request, body, execution) -> {
            RequestIdContext.get().ifPresent(requestId -> {
                if (!request.getHeaders().containsHeader(RequestIdHeaders.REQUEST_ID)) {
                    request.getHeaders().set(RequestIdHeaders.REQUEST_ID, requestId);
                }
            });
            return execution.execute(request, body);
        });
    }
}
