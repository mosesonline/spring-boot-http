package de.mosesonline.http;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.junit.jupiter.Container;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Container
    static final WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.10.0")
            .withoutBanner()
            .withLogConsumer(of -> LoggerFactory.getLogger(TestcontainersConfiguration.class)
                    .info(of.getUtf8String()));

    static {
        wiremockServer.start();
    }

    @Bean
    DynamicPropertyRegistrar registerResourceServerIssuerProperty() {
        return (registry) -> registry.add("backend.host.url", () -> wiremockServer.getBaseUrl());
    }
}
