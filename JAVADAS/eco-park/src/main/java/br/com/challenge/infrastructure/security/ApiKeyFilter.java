package br.com.challenge.infrastructure.security;

import br.com.challenge.application.service.ApiKeyValidator;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApiKeyFilter implements ContainerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";

    @Inject
    ApiKeyValidator apiKeyValidator;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (isPublicEndpoint(requestContext)) {
            return;
        }

        if (!apiKeyValidator.isValid(requestContext.getHeaderString(API_KEY_HEADER))) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Chave de API inválida!\"}")
                    .build());
        }
    }

    private boolean isPublicEndpoint(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        return path.contains("/health") ||
                path.contains("/openapi") ||
                path.contains("/swagger") ||
                path.contains("/q/") ||
                path.contains("/meu-nome/") ||
                (path.contains("/login") && "POST".equals(method)) ||
                "OPTIONS".equals(method);
    }
}