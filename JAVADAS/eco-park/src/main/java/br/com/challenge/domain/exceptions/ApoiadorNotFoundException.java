package br.com.challenge.domain.exceptions;

public class ApoiadorNotFoundException extends br.com.challenge.domain.exceptions.DomainException {
    public ApoiadorNotFoundException(Long id) {
        super("Apoiador não encontrado com ID: " + id, "APOIADOR_NOT_FOUND");
    }
}
