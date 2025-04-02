package de.mosesonline.http.tracing;

import brave.baggage.BaggageField;
import brave.baggage.CorrelationScopeConfig;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.CurrentTraceContext;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TracingConfiguration {
    @Bean
    BaggageField sessionId() {
        return BaggageField.create("x-session-id");
    }

    @Bean
    CurrentTraceContext.ScopeDecorator mdcScopeDecorator(BaggageField sessionId) {
        return MDCScopeDecorator.newBuilder()
                .clear()
                .add(CorrelationScopeConfig.SingleCorrelationField.newBuilder(sessionId)
                        .flushOnUpdate()
                        .build())
                .build();
    }

    @Bean
    ObservedAspect observedAspect(ObservationRegistry registry) {
        return new ObservedAspect(registry);
    }
}
