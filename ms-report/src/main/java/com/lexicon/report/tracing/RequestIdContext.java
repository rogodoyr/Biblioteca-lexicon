package com.lexicon.report.tracing;

import java.util.Optional;
import org.slf4j.MDC;

public final class RequestIdContext {

    public static final String MDC_KEY = "requestId";

    private RequestIdContext() {
    }

    public static void set(String requestId) {
        MDC.put(MDC_KEY, requestId);
    }

    public static Optional<String> get() {
        return Optional.ofNullable(MDC.get(MDC_KEY));
    }

    public static String getOrUnknown() {
        return get().orElse("unknown");
    }

    public static void clear() {
        MDC.remove(MDC_KEY);
    }
}
