package br.com.challenge.domain.exceptions;

public class PacienteNotFoundExceptionImpl extends PacienteNotFoundException {
    public PacienteNotFoundExceptionImpl(Long id) {
        super(id);
    }
}
