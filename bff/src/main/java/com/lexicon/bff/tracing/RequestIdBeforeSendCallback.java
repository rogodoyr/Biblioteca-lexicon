package com.lexicon.bff.tracing;

import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import org.springframework.stereotype.Component;

@Component
public class RequestIdBeforeSendCallback implements SentryOptions.BeforeSendCallback {

    @Override
    public SentryEvent execute(SentryEvent event, Hint hint) {
        RequestIdContext.get().ifPresent(requestId -> RequestIdEventSupport.enrich(event, requestId));
        return event;
    }
}
