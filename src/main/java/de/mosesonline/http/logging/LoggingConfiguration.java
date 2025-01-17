package de.mosesonline.http.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import java.util.List;

import static org.zalando.logbook.json.JsonPathBodyFilters.jsonPath;

@Configuration
class LoggingConfiguration {

    @Bean
    Logbook logbook(ObjectMapper objectMapper) {
        return Logbook.builder()
                .bodyFilters(List.of(jsonPath("$.data").replace("replacement"), jsonPath("$.decimalData").replace("-1234")))
                .sink(new DefaultSink(new JsonHttpLogFormatter(objectMapper), new DefaultHttpLogWriter()))
                .build();
    }
}
