package br.com.challenge.domain.service;

import br.com.challenge.domain.exceptions.TeleconsultaNotFoundException;
import br.com.challenge.domain.exceptions.ValidationException;
import br.com.challenge.domain.exceptions.ConcurrentModificationException;
import br.com.challenge.domain.exceptions.PacienteNotFoundException;
import br.com.challenge.domain.logging.Logger;
import br.com.challenge.domain.model.Teleconsulta;
import br.com.challenge.domain.repository.TeleconsultaRepository;
import br.com.challenge.domain.repository.PacienteRepository;
import br.com.challenge.infrastructure.logging.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class TeleconsultaService {

    private static final Logger logger = LoggerFactory.getLogger(TeleconsultaService.class);

    @Inject
    TeleconsultaRepository teleconsultaRepository;

    @Inject
    PacienteRepository pacienteRepository;

    public List<Teleconsulta> listarTodas() {
        logger.info("Listando todas as teleconsultas");
        return teleconsultaRepository.listarTodas();
    }

    public Teleconsulta buscarPorId(Long id) {
        logger.debug("Buscando teleconsulta por ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Tentativa de buscar teleconsulta com ID inválido: {}", id);
            throw new ValidationException("ID da teleconsulta é inválido");
        }

        return teleconsultaRepository.buscarPorId(id)
                .orElseThrow(() -> {
                    logger.warn("Teleconsulta não encontrada com ID: {}", id);
                    return new TeleconsultaNotFoundException(id);
                });
    }

    public Teleconsulta agendar(@Valid Teleconsulta teleconsulta) {
        logger.info("Tentativa de agendamento de teleconsulta para paciente: {}", teleconsulta.getPacienteId());

        if (pacienteRepository.buscarPorId(teleconsulta.getPacienteId()).isEmpty()) {
            logger.warn("Paciente não encontrado: {}", teleconsulta.getPacienteId());
            throw new PacienteNotFoundException(teleconsulta.getPacienteId());
        }

        if (teleconsulta.getDataHora().isBefore(LocalDateTime.now())) {
            logger.warn("Tentativa de agendar teleconsulta no passado: {}", teleconsulta.getDataHora());
            throw new ValidationException("Data e hora da consulta devem ser no futuro");
        }

        if (!teleconsultaRepository.horarioDisponivel(teleconsulta.getDataHora(), teleconsulta.getMedico())) {
            logger.warn("Médico {} não disponível no horário {}", teleconsulta.getMedico(), teleconsulta.getDataHora());
            throw new ValidationException("Médico não disponível no horário selecionado");
        }

        if (teleconsulta.getStatus() == null) {
            teleconsulta.setStatus("AGENDADA");
        } else if (!teleconsulta.getStatus().matches("AGENDADA|REALIZADA|CANCELADA")) {
            throw new ValidationException("Status inválido. Deve ser: AGENDADA, REALIZADA ou CANCELADA");
        }

        Teleconsulta agendada = teleconsultaRepository.salvar(teleconsulta);
        logger.info("Teleconsulta agendada com ID: {} para paciente: {}", agendada.getId(), agendada.getPacienteId());
        return agendada;
    }

    public Teleconsulta atualizar(Long id, @Valid Teleconsulta teleconsulta) {
        logger.info("Tentativa de atualização da teleconsulta ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Tentativa de atualizar teleconsulta com ID inválido: {}", id);
            throw new ValidationException("ID da teleconsulta é inválido");
        }

        Teleconsulta existente = buscarPorId(id);

        if (pacienteRepository.buscarPorId(teleconsulta.getPacienteId()).isEmpty()) {
            throw new PacienteNotFoundException(teleconsulta.getPacienteId());
        }

        if (!existente.getDataHora().equals(teleconsulta.getDataHora()) ||
                !existente.getMedico().equals(teleconsulta.getMedico())) {

            if (!teleconsultaRepository.horarioDisponivel(teleconsulta.getDataHora(), teleconsulta.getMedico())) {
                throw new ValidationException("Médico não disponível no horário selecionado");
            }
        }

        teleconsulta.setId(id);
        teleconsulta.setVersao(existente.getVersao() + 1);

        boolean atualizado = teleconsultaRepository.atualizar(teleconsulta);

        if (!atualizado) {
            logger.error("Falha ao atualizar teleconsulta ID: {} - Versão conflitante", id);
            throw new ConcurrentModificationException("Teleconsulta", id, existente.getVersao());
        }

        logger.info("Teleconsulta atualizada com sucesso ID: {}, nova versão: {}", id, teleconsulta.getVersao());
        return teleconsulta;
    }

    public void cancelar(Long id) {
        logger.info("Cancelando teleconsulta ID: {}", id);

        if (id == null || id <= 0) {
            throw new ValidationException("ID da teleconsulta é inválido");
        }

        buscarPorId(id);

        boolean cancelada = teleconsultaRepository.atualizarStatus(id, "CANCELADA");
        if (!cancelada) {
            throw new RuntimeException("Erro ao cancelar teleconsulta");
        }

        logger.info("Teleconsulta ID: {} cancelada com sucesso", id);
    }

    public void deletar(Long id) {
        logger.info("Deletando teleconsulta ID: {}", id);

        if (id == null || id <= 0) {
            throw new ValidationException("ID da teleconsulta é inválido");
        }

        buscarPorId(id);

        boolean deletada = teleconsultaRepository.deletar(id);
        if (!deletada) {
            throw new RuntimeException("Erro ao deletar teleconsulta");
        }

        logger.info("Teleconsulta ID: {} deletada com sucesso", id);
    }

    public List<Teleconsulta> buscarPorPaciente(Long pacienteId) {
        logger.debug("Buscando teleconsultas por paciente ID: {}", pacienteId);

        if (pacienteId == null || pacienteId <= 0) {
            throw new ValidationException("ID do paciente é inválido");
        }

        if (pacienteRepository.buscarPorId(pacienteId).isEmpty()) {
            throw new PacienteNotFoundException(pacienteId);
        }

        return teleconsultaRepository.buscarPorPaciente(pacienteId);
    }

    public List<Teleconsulta> buscarPorMedico(String medico) {
        if (medico == null || medico.trim().isEmpty()) {
            throw new ValidationException("Nome do médico não pode ser vazio");
        }
        return teleconsultaRepository.buscarPorMedico(medico);
    }

    public List<Teleconsulta> buscarPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("Status não pode ser vazio");
        }

        if (!status.matches("AGENDADA|REALIZADA|CANCELADA")) {
            throw new ValidationException("Status inválido. Deve ser: AGENDADA, REALIZADA ou CANCELADA");
        }

        return teleconsultaRepository.buscarPorStatus(status);
    }

    public List<Teleconsulta> consultasDeHoje() {
        logger.info("Buscando consultas de hoje");
        return teleconsultaRepository.consultasDeHoje();
    }

    public void atualizarStatus(Long id, String novoStatus) {
        logger.info("Atualizando status da teleconsulta ID: {} para {}", id, novoStatus);

        if (id == null || id <= 0) {
            throw new ValidationException("ID da teleconsulta é inválido");
        }

        if (novoStatus == null || !novoStatus.matches("AGENDADA|REALIZADA|CANCELADA")) {
            throw new ValidationException("Status inválido. Deve ser: AGENDADA, REALIZADA ou CANCELADA");
        }

        buscarPorId(id);

        boolean atualizado = teleconsultaRepository.atualizarStatus(id, novoStatus);
        if (!atualizado) {
            throw new RuntimeException("Erro ao atualizar status da teleconsulta");
        }

        logger.info("Status da teleconsulta ID: {} atualizado para {}", id, novoStatus);
    }

    public void adicionarObservacoes(Long id, String observacoes) {
        logger.info("Adicionando observações à teleconsulta ID: {}", id);

        if (id == null || id <= 0) {
            throw new ValidationException("ID da teleconsulta é inválido");
        }

        if (observacoes != null && observacoes.length() > 500) {
            throw new ValidationException("Observações não podem ter mais de 500 caracteres");
        }

        buscarPorId(id);

        boolean adicionado = teleconsultaRepository.adicionarObservacoes(id, observacoes);
        if (!adicionado) {
            throw new RuntimeException("Erro ao adicionar observações à teleconsulta");
        }

        logger.info("Observações adicionadas à teleconsulta ID: {}", id);
    }

    public boolean verificarDisponibilidade(LocalDateTime dataHora, String medico) {
        logger.debug("Verificando disponibilidade para médico {} no horário {}", medico, dataHora);

        if (dataHora == null || medico == null || medico.trim().isEmpty()) {
            throw new ValidationException("Data/hora e médico são obrigatórios");
        }

        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Data e hora devem ser no futuro");
        }

        return teleconsultaRepository.horarioDisponivel(dataHora, medico);
    }

    public int contarPorStatus(String status) {
        if (status == null || !status.matches("AGENDADA|REALIZADA|CANCELADA")) {
            throw new ValidationException("Status inválido");
        }
        return teleconsultaRepository.contarPorStatus(status);
    }

    public int contarTotal() {
        return teleconsultaRepository.contarTotal();
    }
}