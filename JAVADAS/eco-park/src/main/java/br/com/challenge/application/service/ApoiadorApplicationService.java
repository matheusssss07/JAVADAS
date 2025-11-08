package br.com.challenge.application.service;

import br.com.challenge.domain.model.Apoiador;
import br.com.challenge.domain.service.ApoiadorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;

@ApplicationScoped
public class ApoiadorApplicationService {

    @Inject
    ApoiadorService apoiadorService;

    public List<Apoiador> listarTodos() {
        return apoiadorService.listarTodos();
    }

    public Apoiador buscarPorId(Long id) {
        return apoiadorService.buscarPorId(id);
    }

    public Apoiador buscarPorCpf(String cpf) {
        return apoiadorService.buscarPorCpf(cpf);
    }

    public Apoiador cadastrar(@Valid Apoiador apoiador) {
        return apoiadorService.cadastrar(apoiador);
    }

    public Apoiador atualizar(Long id, @Valid Apoiador apoiador) {
        return apoiadorService.atualizar(id, apoiador);
    }

    public void deletar(Long id) {
        apoiadorService.deletar(id);
    }

    public List<Apoiador> buscarPorCargo(String cargo) {
        return apoiadorService.buscarPorCargo(cargo);
    }

    public List<String> listarCargos() {
        return apoiadorService.listarCargos();
    }

    public void vincularPaciente(Long apoiadorId, Long pacienteId) {
        apoiadorService.vincularPaciente(apoiadorId, pacienteId);
    }

    public void desvincularPaciente(Long pacienteId) {
        apoiadorService.desvincularPaciente(pacienteId);
    }

    public int contarTotalApoiadores() {
        return apoiadorService.contarTotalApoiadores();
    }

    public int contarPacientesVinculados(Long apoiadorId) {
        return apoiadorService.contarPacientesVinculados(apoiadorId);
    }
}