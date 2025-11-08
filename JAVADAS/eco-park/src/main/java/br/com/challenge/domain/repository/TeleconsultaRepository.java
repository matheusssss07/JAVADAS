package br.com.challenge.domain.repository;

import br.com.challenge.domain.model.Teleconsulta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TeleconsultaRepository {

    List<Teleconsulta> listarTodas();
    Optional<Teleconsulta> buscarPorId(Long id);
    Teleconsulta salvar(Teleconsulta teleconsulta);
    boolean atualizar(Teleconsulta teleconsulta);
    boolean deletar(Long id);

    List<Teleconsulta> buscarPorPaciente(Long pacienteId);
    List<Teleconsulta> buscarPorMedico(String medico);
    List<Teleconsulta> buscarPorStatus(String status);
    List<Teleconsulta> consultasDeHoje();

    boolean horarioDisponivel(LocalDateTime dataHora, String medico);

    boolean atualizarStatus(Long id, String novoStatus);
    boolean adicionarObservacoes(Long id, String observacoes);

    int contarPorStatus(String status);
    int contarTotal();
}