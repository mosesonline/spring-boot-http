package de.mosesonline.http.routing;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.api.BackendService;
import de.mosesonline.http.api.QualifiedBackendService;
import de.mosesonline.http.api.SessionBackendPort;
import de.mosesonline.http.model.BackendRequestContext;
import de.mosesonline.http.model.exception.BackendUnknownException;
import io.github.resilience4j.springboot3.nativeimage.configuration.NativeHintsConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(NativeHintsConfiguration.class)
public class BackendRoutingConfiguration {
    public final static String BACKEND_QUALIFIER = "requested-backend-service";
    private final ApplicationContext applicationContext;
    private final Map<String, BackendPort> backendServiceBeans;
    private final Map<String, SessionBackendPort> sessionBackendPortsBeans;

    BackendRoutingConfiguration(ApplicationContext applicationContext,
                                @BackendService
                                Map<String, BackendPort> backendServiceBeans,
                                @BackendService
                                Map<String, SessionBackendPort> sessionBackendPortsBeans) {
        this.applicationContext = applicationContext;
        this.backendServiceBeans = backendServiceBeans;
        this.sessionBackendPortsBeans = sessionBackendPortsBeans;
    }

    @Bean
    @RequestScope
    BackendPort backendService(BackendRequestContext backendRequestContext) {
        return getPort(backendRequestContext, backendServiceBeans);
    }

    @Bean
    @RequestScope
    SessionBackendPort sessionBackendService(BackendRequestContext backendRequestContext) {
        return getPort(backendRequestContext, sessionBackendPortsBeans);
    }

    private <T> T getPort(BackendRequestContext backendRequestContext, Map<String, T> ports) {
        for (Map.Entry<String, T> entry : ports.entrySet()) {
            final var backendService = applicationContext.findAnnotationOnBean(entry.getKey(), QualifiedBackendService.class);

            if (backendService != null && backendService.backendId().equals(backendRequestContext.getBackendDiscriminator())) {
                return entry.getValue();
            } else {
                final var qualifiedBean = applicationContext.findAnnotationOnBean(entry.getKey(), Qualifier.class);
                if (qualifiedBean != null && qualifiedBean.value().equals(backendRequestContext.getBackendDiscriminator())) {
                    return entry.getValue();
                }
            }
        }
        throw new BackendUnknownException("Cannot find the backend with id: " + backendRequestContext.getBackendDiscriminator());
    }
}
