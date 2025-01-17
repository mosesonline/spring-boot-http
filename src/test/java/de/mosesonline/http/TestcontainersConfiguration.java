package de.mosesonline.http;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@TestConfiguration(proxyBeanMethods = false)
@Testcontainers
class TestcontainersConfiguration {
    @Container
    static final WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.10.0")
            .withoutBanner()
            .withCliArg("--verbose")
            .withCliArg("--print-all-network-traffic")
            .withCliArg("--global-response-templating")
            .withLogConsumer(of -> LoggerFactory.getLogger(TestcontainersConfiguration.class)
                    .trace(of.getUtf8String()))
            .withMappingFromJSON("""
                    {
                         "request": {
                             "method": "GET",
                             "urlPathTemplate": "/test/{greet}"
                         },
                         "response": {
                             "status": 200,
                             "jsonBody": {
                                 "data": "{{request.path.greet}}",
                                 "dateTime": "{{now}}",
                                 "decimalData": 1.23,
                                 "status": "OK"
                             },
                             "headers": {
                                 "Content-Type": "application/json",
                                 "Cache-Control": "no-cache"
                             }
                         }
                     }""");
    static final DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:4.0.3");

    @Container
    static final LocalStackContainer localstack = new LocalStackContainer(localstackImage)
            .withServices(DYNAMODB)
            .withCopyFileToContainer(
                    MountableFile.forHostPath("src/test/resources/db-init/init-dynamodb.sh"),
                    "/etc/localstack/init/ready.d/init-resources.sh"
            );


    static {
        wiremockServer.start();
        localstack.start();
    }

    @Bean
    DynamicPropertyRegistrar registerResourceServerIssuerProperty() {
        return (registry) -> {
            registry.add("backend.host.url", wiremockServer::getBaseUrl);
            registry.add("aws.dynamodb.accessKey", localstack::getAccessKey);
            registry.add("aws.dynamodb.secretKey", localstack::getSecretKey);
            registry.add("aws.dynamodb.region", localstack::getRegion);
            registry.add("aws.dynamodb.endpoint", localstack::getEndpoint);
        };
    }
}
