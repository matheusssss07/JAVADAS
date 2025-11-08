package br.com.challenge.infrastructure.security;

import br.com.challenge.application.service.ApiKeyValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ApiKeyValidatorImpl implements ApiKeyValidator {

    private final String validApiKey;

    @Inject
    public ApiKeyValidatorImpl(@ConfigProperty(name = "api.key") String validApiKey) {
        this.validApiKey = validApiKey;
    }

    @Override
    public boolean isValid(String apiKey) {
        return isPresent(apiKey) && this.validApiKey.equals(apiKey);
    }

    @Override
    public boolean isPresent(String apiKey) {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}