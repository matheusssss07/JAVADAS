package br.com.challenge.application.service;

public interface ApiKeyValidator {
    boolean isValid(String apiKey);
    boolean isPresent(String apiKey);
}
