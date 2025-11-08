package br.com.challenge.domain.exceptions;

public class UsuarioNotFoundExceptionImpl extends br.com.challenge.domain.exceptions.UsuarioNotFoundException {
    public UsuarioNotFoundExceptionImpl(String message) {
        super(message);}
}
