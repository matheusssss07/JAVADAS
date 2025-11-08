package br.com.challenge.domain.exceptions;

/**
 * Exceção lançada quando ocorre conflito de concorrência
 * (quando dois usuários tentam modificar o mesmo recurso simultaneamente)
 */
public class ConcurrentModificationException extends DomainException {

    public ConcurrentModificationException(String message) {
        super(message, "CONCURRENT_MODIFICATION");
    }

    public ConcurrentModificationException(String entity, Long id, Long currentVersion) {
        super(String.format(
                "%s com ID %d foi modificado por outro usuário. Versão atual: %d. Recarregue os dados e tente novamente.",
                entity, id, currentVersion
        ), "CONCURRENT_MODIFICATION");
    }

    public ConcurrentModificationException(String entity, Long id, Long expectedVersion, Long actualVersion) {
        super(String.format(
                "%s com ID %d foi modificado. Versão esperada: %d, Versão atual: %d",
                entity, id, expectedVersion, actualVersion
        ), "CONCURRENT_MODIFICATION");
    }
}