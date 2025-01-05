package de.mosesonline.http.session.web;

import de.mosesonline.http.session.RawRequestSession;
import jakarta.servlet.ServletContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Objects;

@Component
@RequestScope
public class RequestSessionHolder {
    private RawRequestSession requestSession;
    private ServletContext servletContext;


    public RawRequestSession getRequestSession() {
        return requestSession;
    }


    public void setRequestSession(RawRequestSession requestSession) {
        this.requestSession = requestSession;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RequestSessionHolder) obj;
        return Objects.equals(this.requestSession, that.requestSession) &&
                Objects.equals(this.servletContext, that.servletContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestSession, servletContext);
    }

    @Override
    public String toString() {
        return "RequestSessionHolder[" +
                "requestSession=" + requestSession + ", " +
                "servletContext=" + servletContext + ']';
    }
}
