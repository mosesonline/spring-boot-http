package de.mosesonline.http.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
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

    private static final IntNode INT_REPLACEMENT = new IntNode(-1234);
    private static final TextNode TEXT_REPLACEMENT = new TextNode("replacement");

    @Bean
    Logbook logbook(ObjectMapper objectMapper) {
        return Logbook.builder()
                .bodyFilters(List.of(jsonPath("$.data").replace(TEXT_REPLACEMENT), jsonPath("$.decimalData").replace(INT_REPLACEMENT)))
                .sink(new DefaultSink(new JsonHttpLogFormatter(objectMapper), new DefaultHttpLogWriter()))
                .build();
    }
}
