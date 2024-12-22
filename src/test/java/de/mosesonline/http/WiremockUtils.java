package de.mosesonline.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static de.mosesonline.http.TestcontainersConfiguration.wiremockServer;
import static org.assertj.core.api.Assertions.assertThat;

public class WiremockUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    static String createMapping(String mapping) throws Exception {
        HttpResponse<String> res;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(mapping, StandardCharsets.UTF_8))
                    .uri(URI.create("http://localhost:" + wiremockServer.getPort() + "/__admin/mappings"))
                    .build();
            res = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(res.statusCode()).isBetween(200, 300);
        }
        return JsonPath.read(res.body(), "uuid");

    }

    static void deleteMapping(String... ids) throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            for (String id : ids) {
                HttpRequest request = HttpRequest.newBuilder()
                        .DELETE()
                        .uri(URI.create("http://localhost:" + wiremockServer.getPort() + "/__admin/mappings/" + id))
                        .build();
                HttpResponse<Void> res = client.send(request, HttpResponse.BodyHandlers.discarding());
                assertThat(res.statusCode()).isBetween(200, 300);
            }
        }
    }

    static void assertThatScenarioHasState(String scenario, String state) throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:" + wiremockServer.getPort() + "/__admin/scenarios"))
                    .build();
            final var body = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
            ScenarioList res = MAPPER.readValue(body, ScenarioList.class);

            assertThat(res.scenarios()).satisfiesOnlyOnce(s -> {
                assertThat(s.id()).isEqualTo(scenario);
                assertThat(s.state()).isEqualTo(state);
            });
        }
    }

    private record ScenarioList(List<Scenario> scenarios) {

    }

    private record Scenario(String id, String name, String state) {
    }
}
