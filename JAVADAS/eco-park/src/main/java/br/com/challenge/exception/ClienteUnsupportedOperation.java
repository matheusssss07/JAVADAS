package br.com.challenge.exception;

public class ClienteUnsupportedOperation extends  RuntimeException {

    public ClienteUnsupportedOperation(String message) {
        super(message);
    }
}
