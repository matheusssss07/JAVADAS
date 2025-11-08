package br.com.challenge.domain.exceptions;

public class TeleconsultaNotFoundException extends DomainException {
    public TeleconsultaNotFoundException(Long id) {
        super("Teleconsulta não encontrada com ID: " + id, "TELECONSULTA_NOT_FOUND");
    }
}
