package de.mosesonline.http;

import de.mosesonline.http.api.BackendPort;
import de.mosesonline.http.api.BackendService;
import de.mosesonline.http.api.SessionBackendPort;
import de.mosesonline.http.model.BackendRequestContext;
import de.mosesonline.http.model.exception.BackendUnknownException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

@Configuration
public class BackendRoutingConfiguration {
    public final static String BACKEND_QUALIFIER = "requested-backend-service";
    private final ApplicationContext applicationContext;

    BackendRoutingConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @Qualifier(BACKEND_QUALIFIER)
    @RequestScope
    BackendPort backendService(BackendRequestContext backendRequestContext) {
        Map<String, BackendPort> beanMap = applicationContext.getBeansOfType(BackendPort.class);

        for (Map.Entry<String, BackendPort> entry : beanMap.entrySet()) {
            final var db = applicationContext.findAnnotationOnBean(entry.getKey(), BackendService.class);
            if (db != null && db.backendId().equals(backendRequestContext.getBackendDiscriminator())) {
                return entry.getValue();
            }
        }
        throw new BackendUnknownException("Cannot find the backend with id: " + backendRequestContext.getBackendDiscriminator());
    }

    @Bean
    @Qualifier(BACKEND_QUALIFIER)
    @RequestScope
    SessionBackendPort sessionBackendService(BackendRequestContext backendRequestContext) {
        Map<String, SessionBackendPort> beanMap = applicationContext.getBeansOfType(SessionBackendPort.class);

        for (Map.Entry<String, SessionBackendPort> entry : beanMap.entrySet()) {
            final var db = applicationContext.findAnnotationOnBean(entry.getKey(), BackendService.class);
            if (db != null && db.backendId().equals(backendRequestContext.getBackendDiscriminator())) {
                return entry.getValue();
            }
        }
        throw new BackendUnknownException("Cannot find the backend with id: " + backendRequestContext.getBackendDiscriminator());
    }
}
