package br.com.challenge.domain.repository;

import br.com.challenge.domain.model.Paciente;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository {

    List<Paciente> listarTodos();
    Optional<Paciente> buscarPorId(Long id);
    Optional<Paciente> buscarPorCpf(String cpf);
    Paciente salvar(Paciente paciente);
    boolean atualizar(Paciente paciente);
    boolean deletar(Long id);

    List<Paciente> buscarPorApoiador(Long apoiadorId);

    int contarTotal();
}