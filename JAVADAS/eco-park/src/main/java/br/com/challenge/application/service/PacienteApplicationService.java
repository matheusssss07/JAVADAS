package br.com.challenge.application.service;

import br.com.challenge.domain.model.Paciente;
import br.com.challenge.domain.service.PacienteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;

@ApplicationScoped
public class PacienteApplicationService {

    @Inject
    PacienteService pacienteService;

    public List<Paciente> listarTodos() {
        return pacienteService.listarTodos();
    }

    public Paciente buscarPorId(Long id) {
        return pacienteService.buscarPorId(id);
    }

    public Paciente buscarPorCpf(String cpf) {
        return pacienteService.buscarPorCpf(cpf);
    }

    public Paciente cadastrar(@Valid Paciente paciente) {
        return pacienteService.cadastrar(paciente);
    }

    public Paciente atualizar(Long id, @Valid Paciente paciente) {
        return pacienteService.atualizar(id, paciente);
    }

    public void deletar(Long id) {
        pacienteService.deletar(id);
    }

    public List<Paciente> buscarPorApoiador(Long apoiadorId) {
        return pacienteService.buscarPorApoiador(apoiadorId);
    }

    public void vincularApoiador(Long pacienteId, Long apoiadorId) {
        pacienteService.vincularApoiador(pacienteId, apoiadorId);
    }

    public void desvincularApoiador(Long pacienteId) {
        pacienteService.desvincularApoiador(pacienteId);
    }

    public int contarTotalPacientes() {
        return pacienteService.contarTotalPacientes();
    }
}