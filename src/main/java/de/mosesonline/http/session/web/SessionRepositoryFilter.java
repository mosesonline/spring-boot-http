package de.mosesonline.http.session.web;

import de.mosesonline.http.session.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SessionRepositoryFilter extends OncePerRequestFilter {

    private static final String X_SESSION_ID_HEADER_NAME = "X-Session-Id";
    private final SessionService sessionService;
    private final RequestSessionHolder requestSessionHolder;

    public SessionRepositoryFilter(SessionService sessionService, RequestSessionHolder requestSessionHolder) {
        this.sessionService = sessionService;
        this.requestSessionHolder = requestSessionHolder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final var session = sessionService.obtainRequestSession(request.getHeader(X_SESSION_ID_HEADER_NAME));
        requestSessionHolder.setServletContext(request.getServletContext());
        requestSessionHolder.setRequestSession(session);
        if (session != null) {
            response.addHeader(X_SESSION_ID_HEADER_NAME, session.getId().toString());
        }

        filterChain.doFilter(request, response);

        sessionService.saveRequestSession(requestSessionHolder.getRequestSession());
    }
}
