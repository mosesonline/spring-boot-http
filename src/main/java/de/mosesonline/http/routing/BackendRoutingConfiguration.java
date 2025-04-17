package de.mosesonline.http.routing;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.api.BackendService;
import de.mosesonline.http.api.QualifiedBackendService;
import de.mosesonline.http.api.SessionBackendPort;
import de.mosesonline.http.model.BackendRequestContext;
import de.mosesonline.http.model.exception.BackendUnknownException;
import io.github.resilience4j.springboot3.nativeimage.configuration.NativeHintsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(NativeHintsConfiguration.class)
public class BackendRoutingConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendRoutingConfiguration.class);
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
    @RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Reflective
    BackendPort backendService(BackendRequestContext backendRequestContext) {
        LOGGER.info("Backend service requested: {}", backendRequestContext.getBackendDiscriminator());
        BackendPort port = getPort(backendRequestContext, backendServiceBeans);
        LOGGER.info("Backend service found: {}", port.getClass());
        return port;
    }

    @Bean
    @RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Reflective
    SessionBackendPort sessionBackendService(BackendRequestContext backendRequestContext) {
        LOGGER.info("Session backend service requested: {}", backendRequestContext.getBackendDiscriminator());
        return getPort(backendRequestContext, sessionBackendPortsBeans);
    }

    private <T> T getPort(BackendRequestContext backendRequestContext, Map<String, T> ports) {
        LOGGER.info("Found beans: {}", ports.size());
        for (Map.Entry<String, T> entry : ports.entrySet()) {
            final var backendService = applicationContext.findAnnotationOnBean(entry.getKey(), QualifiedBackendService.class);
            LOGGER.info("Found bean with QualifiedBackendService: {}", backendService);

            if (backendService != null && backendService.backendId().equals(backendRequestContext.getBackendDiscriminator())) {
                LOGGER.info("Selected bean with QualifiedBackendService: {}", backendService);
                return entry.getValue();
            } else {
                final var qualifiedBean = applicationContext.findAnnotationOnBean(entry.getKey(), Qualifier.class);
                LOGGER.info("Found bean with Qualifier: {}", qualifiedBean);
                if (qualifiedBean != null && qualifiedBean.value().equals(backendRequestContext.getBackendDiscriminator())) {
                    LOGGER.info("Selected bean with Qualifier: {}", qualifiedBean);
                    return entry.getValue();
                }
            }
        }
        throw new BackendUnknownException("Cannot find the backend with id: " + backendRequestContext.getBackendDiscriminator());
    }
}
