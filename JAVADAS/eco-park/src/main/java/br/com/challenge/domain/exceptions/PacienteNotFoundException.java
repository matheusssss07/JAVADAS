package br.com.challenge.domain.exceptions;

public class PacienteNotFoundException extends DomainException {
    public PacienteNotFoundException(Long id) {
        super("Paciente não encontrado com ID: " + id, "PACIENTE_NOT_FOUND");
    }
}
