package br.com.challenge.domain.repository;

import br.com.challenge.domain.model.Apoiador;
import java.util.List;
import java.util.Optional;

public interface ApoiadorRepository {


    List<Apoiador> listarTodos();
    Optional<Apoiador> buscarPorId(Long id);
    Optional<Apoiador> buscarPorCpf(String cpf);
    Apoiador salvar(Apoiador apoiador);
    boolean atualizar(Apoiador apoiador);
    boolean deletar(Long id);


    List<Apoiador> buscarPorCargo(String cargo);
    List<String> listarCargos();


    boolean vincularPaciente(Long apoiadorId, Long pacienteId);
    boolean desvincularPaciente(Long pacienteId);
    boolean temPacientesVinculados(Long apoiadorId);


    int contarTotal();
}