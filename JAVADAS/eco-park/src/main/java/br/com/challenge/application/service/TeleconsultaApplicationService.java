package br.com.challenge.application.service;

import br.com.challenge.domain.model.Teleconsulta;
import br.com.challenge.domain.service.TeleconsultaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class TeleconsultaApplicationService {

    @Inject
    TeleconsultaService teleconsultaService;

    public List<Teleconsulta> listarTodas() {
        return teleconsultaService.listarTodas();
    }

    public Teleconsulta buscarPorId(Long id) {
        return teleconsultaService.buscarPorId(id);
    }

    public Teleconsulta agendar(@Valid Teleconsulta teleconsulta) {
        return teleconsultaService.agendar(teleconsulta);
    }

    public Teleconsulta atualizar(Long id, @Valid Teleconsulta teleconsulta) {
        return teleconsultaService.atualizar(id, teleconsulta);
    }

    public void cancelar(Long id) {
        teleconsultaService.cancelar(id);
    }

    public void deletar(Long id) {
        teleconsultaService.deletar(id);
    }

    public List<Teleconsulta> buscarPorPaciente(Long pacienteId) {
        return teleconsultaService.buscarPorPaciente(pacienteId);
    }

    public List<Teleconsulta> buscarPorMedico(String medico) {
        return teleconsultaService.buscarPorMedico(medico);
    }

    public List<Teleconsulta> buscarPorStatus(String status) {
        return teleconsultaService.buscarPorStatus(status);
    }

    public List<Teleconsulta> consultasDeHoje() {
        return teleconsultaService.consultasDeHoje();
    }

    public void atualizarStatus(Long id, String novoStatus) {
        teleconsultaService.atualizarStatus(id, novoStatus);
    }

    public void adicionarObservacoes(Long id, String observacoes) {
        teleconsultaService.adicionarObservacoes(id, observacoes);
    }

    public boolean verificarDisponibilidade(LocalDateTime dataHora, String medico) {
        return teleconsultaService.verificarDisponibilidade(dataHora, medico);
    }

    public int contarPorStatus(String status) {
        return teleconsultaService.contarPorStatus(status);
    }

    public int contarTotal() {
        return teleconsultaService.contarTotal();
    }
}