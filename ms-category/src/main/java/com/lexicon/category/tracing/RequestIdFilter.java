package com.lexicon.category.tracing;

import io.sentry.Sentry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        RequestIdContext.set(requestId);
        response.setHeader(RequestIdHeaders.REQUEST_ID, requestId);
        Sentry.configureScope(scope -> {
            scope.setTag("request_id", requestId);
            scope.setTag("request.id", requestId);
            scope.setExtra("request_id", requestId);
        });
        Sentry.setAttribute("request_id", requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestIdContext.clear();
        }
    }

    private static String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(RequestIdHeaders.REQUEST_ID);
        if (!StringUtils.hasText(requestId)) {
            requestId = request.getHeader(RequestIdHeaders.CORRELATION_ID);
        }
        if (!StringUtils.hasText(requestId)) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId.trim();
    }
}
