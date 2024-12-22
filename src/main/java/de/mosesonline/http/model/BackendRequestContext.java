package de.mosesonline.http.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class BackendRequestContext {
    private String backendDiscriminator;

    public String getBackendDiscriminator() {
        return backendDiscriminator;
    }

    public void setBackendDiscriminator(String backendDiscriminator) {
        this.backendDiscriminator = backendDiscriminator;
    }
}
