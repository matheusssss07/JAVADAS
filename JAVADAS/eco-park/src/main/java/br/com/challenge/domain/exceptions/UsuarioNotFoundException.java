package br.com.challenge.domain.exceptions;

public class UsuarioNotFoundException extends br.com.challenge.domain.exceptions.DomainException {
    public UsuarioNotFoundException(Long id) {
        super("Usuário não encontrado com ID: " + id, "USUARIO_NOT_FOUND");
    }

    public UsuarioNotFoundException(String cpf) {
        super("Usuário não encontrado com CPF: " + cpf, "USUARIO_NOT_FOUND");
    }
}
