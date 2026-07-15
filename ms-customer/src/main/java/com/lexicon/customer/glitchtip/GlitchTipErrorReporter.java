package com.lexicon.customer.glitchtip;

import com.lexicon.customer.tracing.RequestIdContext;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GlitchTipErrorReporter {

    private static final Logger logger = LoggerFactory.getLogger(GlitchTipErrorReporter.class);

    public void captureException(Throwable throwable) {
        logger.error("Error capturado para envio a GlitchTip [requestId={}]", RequestIdContext.getOrUnknown(), throwable);
        Sentry.withScope(scope -> {
            enrichScope(scope);
            Sentry.captureException(throwable);
        });
    }

    public void captureException(Throwable throwable, String message) {
        logger.error("{} [requestId={}]: {}", message, RequestIdContext.getOrUnknown(), throwable.getMessage(), throwable);
        Sentry.withScope(scope -> {
            scope.setExtra("context", message);
            enrichScope(scope);
            Sentry.captureException(throwable);
        });
    }

    public void captureMessage(String message, SentryLevel level) {
        logger.atLevel(toSlf4jLevel(level)).log("{} [requestId={}]", message, RequestIdContext.getOrUnknown());
        Sentry.withScope(scope -> {
            enrichScope(scope);
            Sentry.captureMessage(message, level);
        });
    }

    public void addBreadcrumb(String message) {
        logger.debug("Breadcrumb [requestId={}]: {}", RequestIdContext.getOrUnknown(), message);
        Sentry.addBreadcrumb(message);
    }

    private static void enrichScope(io.sentry.IScope scope) {
        String requestId = RequestIdContext.getOrUnknown();
        scope.setTag("request_id", requestId);
        scope.setTag("request.id", requestId);
        scope.setExtra("request_id", requestId);
    }

    public void flush() {
        Sentry.flush(2000);
    }

    private static org.slf4j.event.Level toSlf4jLevel(SentryLevel level) {
        return switch (level) {
            case DEBUG -> org.slf4j.event.Level.DEBUG;
            case INFO -> org.slf4j.event.Level.INFO;
            case WARNING -> org.slf4j.event.Level.WARN;
            case ERROR, FATAL -> org.slf4j.event.Level.ERROR;
            default -> org.slf4j.event.Level.INFO;
        };
    }
}
