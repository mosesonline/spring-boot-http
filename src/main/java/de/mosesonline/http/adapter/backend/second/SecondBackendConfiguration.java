package de.mosesonline.http.adapter.backend.second;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class SecondBackendConfiguration {
    @Bean
    SecondBackendClient secondBackendClient(@Value("${backend.host.url}") String backendHostUrl) {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(2))
                .build();
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create().setDefaultSocketConfig(socketConfig).build();
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(2_000);
        clientHttpRequestFactory.setConnectionRequestTimeout(2_000);
        clientHttpRequestFactory.setHttpClient(HttpClientBuilder.create().setConnectionManager(connectionManager).build());
        final var restClient = RestClient.builder()
                .baseUrl(backendHostUrl)
                .requestFactory(clientHttpRequestFactory)
                .build();
        final var adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter)
                .build();
        return factory.createClient(SecondBackendClient.class);
    }
}
