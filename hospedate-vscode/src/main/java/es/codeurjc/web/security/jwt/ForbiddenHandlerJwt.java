package es.codeurjc.web.security.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "message: %s, path: %s".formatted(accessDeniedException.getMessage(), request.getServletPath()));
    }
}
