package de.mosesonline.http.adapter.incoming;

import de.mosesonline.http.model.BackendRequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class BackendRequestContextFilter extends OncePerRequestFilter {
    private final BackendRequestContext backendRequestContext;

    public BackendRequestContextFilter(BackendRequestContext backendRequestContext) {
        this.backendRequestContext = backendRequestContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String backendDiscriminator = request.getHeader("x-backend");
        if (backendDiscriminator != null && !backendDiscriminator.isBlank()) {
            backendRequestContext.setBackendDiscriminator(backendDiscriminator);
        } else {
            throw new ServletException("Backend discriminator is missing.");
        }
        filterChain.doFilter(request, response);
    }
}
