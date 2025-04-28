package de.mosesonline.http.adapter.incoming;

import de.mosesonline.http.model.BackendRequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class BackendRequestContextFilter extends OncePerRequestFilter {
    public static final String X_BACKEND_KEY = "x-backend";
    private final BackendRequestContext backendRequestContext;

    BackendRequestContextFilter(BackendRequestContext backendRequestContext) {
        this.backendRequestContext = backendRequestContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String backendDiscriminator = request.getHeader(X_BACKEND_KEY);
        if (backendDiscriminator != null && !backendDiscriminator.isBlank()) {
            MDC.put(X_BACKEND_KEY, backendDiscriminator);
            backendRequestContext.setBackendDiscriminator(backendDiscriminator);
        } else {
            throw new ServletException("Backend discriminator is missing.");
        }
        filterChain.doFilter(request, response);
    }
}
