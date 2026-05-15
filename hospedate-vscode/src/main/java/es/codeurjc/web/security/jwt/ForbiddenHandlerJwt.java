package es.codeurjc.web.security.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class ForbiddenHandlerJwt implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(ForbiddenHandlerJwt.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        logger.info("Forbidden error: {}", accessDeniedException.getMessage());

        // Write JSON directly so /error is not invoked and HTML is never returned
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"%s\",\"path\":\"%s\"}"
                .formatted(escape(accessDeniedException.getMessage()), escape(request.getServletPath()))
        );
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
