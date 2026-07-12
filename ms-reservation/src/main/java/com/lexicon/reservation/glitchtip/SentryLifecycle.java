package com.lexicon.reservation.glitchtip;

import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;

@Component
public class SentryLifecycle {

    private final GlitchTipErrorReporter errorReporter;

    public SentryLifecycle(GlitchTipErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @PreDestroy
    public void onShutdown() {
        errorReporter.flush();
    }
}
