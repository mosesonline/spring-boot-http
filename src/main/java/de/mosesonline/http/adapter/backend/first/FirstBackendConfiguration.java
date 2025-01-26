package de.mosesonline.http.adapter.backend.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class FirstBackendConfiguration {
    private static final ObjectMapper FIRST_BACKEND_OM = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);

    @Bean
    FirstBackendClient firstBackendClient(@Value("${backend.host.url}") String backendHostUrl) {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(10))
                .build();
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create().setDefaultSocketConfig(socketConfig).build();
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(20_000);
        clientHttpRequestFactory.setConnectionRequestTimeout(20_000);
        clientHttpRequestFactory.setHttpClient(HttpClientBuilder.create().setConnectionManager(connectionManager).build());
        final var restClient = RestClient.builder()
                .baseUrl(backendHostUrl)
                .requestFactory(clientHttpRequestFactory)
                .messageConverters(c -> {
                    // Remove any existing MappingJackson2HttpMessageConverter
                    c.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                    // Add MappingJackson2HttpMessageConverter
                    c.add(new MappingJackson2HttpMessageConverter(FIRST_BACKEND_OM));
                })
                .build();
        final var adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter)
                .build();
        return factory.createClient(FirstBackendClient.class);
    }
}
