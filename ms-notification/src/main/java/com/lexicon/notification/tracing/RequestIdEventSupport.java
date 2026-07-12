package com.lexicon.notification.tracing;

import io.sentry.SentryEvent;
import io.sentry.protocol.Message;
import io.sentry.protocol.SentryException;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class RequestIdEventSupport {

    private static final String MARKER_PREFIX = " [requestId=";

    private RequestIdEventSupport() {
    }

    static void enrich(SentryEvent event, String requestId) {
        event.setTag("request_id", requestId);
        event.setTag("request.id", requestId);
        event.setExtra("request_id", requestId);

        if (event.getExceptions() != null) {
            for (SentryException exception : event.getExceptions()) {
                appendMarker(exception::getValue, exception::setValue, requestId);
            }
        }

        Message message = event.getMessage();
        if (message != null) {
            appendMarker(message::getFormatted, message::setFormatted, requestId);
        }
    }

    private static void appendMarker(
            Supplier<String> getter,
            Consumer<String> setter,
            String requestId) {
        String current = getter.get();
        if (current == null || current.contains(MARKER_PREFIX)) {
            return;
        }
        setter.accept(current + MARKER_PREFIX + requestId + "]");
    }
}
