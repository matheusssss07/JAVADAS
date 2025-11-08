package br.com.challenge.domain.exceptions;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
}