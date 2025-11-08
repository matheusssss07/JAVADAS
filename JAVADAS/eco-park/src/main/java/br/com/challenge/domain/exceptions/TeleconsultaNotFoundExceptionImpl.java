package br.com.challenge.domain.exceptions;

public class TeleconsultaNotFoundExceptionImpl extends TeleconsultaNotFoundException {
    public TeleconsultaNotFoundExceptionImpl(Long id) {
        super(id);
    }
}
