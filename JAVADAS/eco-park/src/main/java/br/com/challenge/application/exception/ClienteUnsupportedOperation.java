package br.com.challenge.application.exception;

public class ClienteUnsupportedOperation extends RuntimeException {
    public ClienteUnsupportedOperation(String message) {
        super(message);
    }
}
