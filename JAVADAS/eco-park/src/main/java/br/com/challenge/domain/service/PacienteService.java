package br.com.challenge.domain.service;

import br.com.challenge.domain.exceptions.PacienteNotFoundException;
import br.com.challenge.domain.exceptions.ValidationException;
import br.com.challenge.domain.exceptions.ConcurrentModificationException;
import br.com.challenge.domain.exceptions.ApoiadorNotFoundException;
import br.com.challenge.domain.logging.Logger;
import br.com.challenge.domain.model.Paciente;
import br.com.challenge.domain.repository.PacienteRepository;
import br.com.challenge.domain.repository.ApoiadorRepository;
import br.com.challenge.infrastructure.logging.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;

@ApplicationScoped
public class PacienteService {

    private static final Logger logger = LoggerFactory.getLogger(PacienteService.class);

    @Inject
    PacienteRepository pacienteRepository;

    @Inject
    ApoiadorRepository apoiadorRepository;

    public List<Paciente> listarTodos() {
        logger.info("Listando todos os pacientes");
        return pacienteRepository.listarTodos();
    }

    public Paciente buscarPorId(Long id) {
        logger.debug("Buscando paciente por ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Tentativa de buscar paciente com ID inválido: {}", id);
            throw new ValidationException("ID do paciente é inválido");
        }

        return pacienteRepository.buscarPorId(id)
                .orElseThrow(() -> {
                    logger.warn("Paciente não encontrado com ID: {}", id);
                    return new PacienteNotFoundException(id);
                });
    }

    public Paciente buscarPorCpf(String cpf) {
        logger.debug("Buscando paciente por CPF: {}", cpf);

        if (cpf == null || cpf.trim().isEmpty()) {
            logger.warn("Tentativa de buscar paciente com CPF vazio");
            throw new ValidationException("CPF não pode ser vazio");
        }

        return pacienteRepository.buscarPorCpf(cpf)
                .orElseThrow(() -> {
                    logger.warn("Paciente não encontrado com CPF: {}", cpf);
                    return new PacienteNotFoundException(0L);
                });
    }

    public Paciente cadastrar(@Valid Paciente paciente) {
        // DEBUG CRÍTICO
        System.out.println("=== DEBUG PACIENTE SERVICE ===");
        System.out.println("Service CPF: " + paciente.getCpf());
        System.out.println("Service Nome: " + paciente.getNomeCompleto());
        System.out.println("Service Idade: " + paciente.getIdade());
        System.out.println("Service TelefoneContato: " + paciente.getTelefoneContato());
        System.out.println("Service Completo: " + paciente.toString());

        logger.info("Tentativa de cadastro de paciente: {}", paciente.getCpf());

        if (pacienteRepository.buscarPorCpf(paciente.getCpf()).isPresent()) {
            logger.warn("CPF já cadastrado: {}", paciente.getCpf());
            throw new ValidationException("CPF já cadastrado no sistema");
        }

        if (paciente.getApoiadorId() != null) {
            if (apoiadorRepository.buscarPorId(paciente.getApoiadorId()).isEmpty()) {
                logger.warn("Apoiador não encontrado: {}", paciente.getApoiadorId());
                throw new ApoiadorNotFoundException(paciente.getApoiadorId());
            }
        }

        try {
            Paciente salvo = pacienteRepository.salvar(paciente);

            System.out.println("=== SUCESSO PACIENTE SERVICE ===");
            System.out.println("Paciente salvo com ID: " + salvo.getId());

            logger.info("Paciente cadastrado com ID: {} e versão: {}", salvo.getId(), salvo.getVersao());
            return salvo;
        } catch (Exception e) {
            System.out.println("=== ERRO NO PACIENTE SERVICE ===");
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar paciente: " + e.getMessage(), e);
        }
    }

    public Paciente atualizar(Long id, @Valid Paciente paciente) {
        logger.info("Tentativa de atualização do paciente ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Tentativa de atualizar paciente com ID inválido: {}", id);
            throw new ValidationException("ID do paciente é inválido");
        }

        Paciente existente = buscarPorId(id);

        pacienteRepository.buscarPorCpf(paciente.getCpf())
                .ifPresent(p -> {
                    if (!p.getId().equals(id)) {
                        logger.warn("CPF {} já cadastrado para outro paciente", paciente.getCpf());
                        throw new ValidationException("CPF já cadastrado para outro paciente");
                    }
                });



        if (paciente.getApoiadorId() != null) {
            if (apoiadorRepository.buscarPorId(paciente.getApoiadorId()).isEmpty()) {
                logger.warn("Apoiador não encontrado: {}", paciente.getApoiadorId());
                throw new ApoiadorNotFoundException(paciente.getApoiadorId());
            }
        }

        paciente.setId(id);
        paciente.setVersao(existente.getVersao() + 1);

        boolean atualizado = pacienteRepository.atualizar(paciente);

        if (!atualizado) {
            logger.error("Falha ao atualizar paciente ID: {} - Versão conflitante", id);
            throw new ConcurrentModificationException("Paciente", id, existente.getVersao());
        }

        logger.info("Paciente atualizado com sucesso ID: {}, nova versão: {}", id, paciente.getVersao());
        return paciente;
    }

    public void deletar(Long id) {
        logger.info("Tentativa de exclusão do paciente ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Tentativa de deletar paciente com ID inválido: {}", id);
            throw new ValidationException("ID do paciente é inválido");
        }

        buscarPorId(id);

        boolean deletado = pacienteRepository.deletar(id);
        if (!deletado) {
            logger.error("Falha ao deletar paciente ID: {}", id);
            throw new RuntimeException("Erro ao deletar paciente");
        }

        logger.info("Paciente deletado com sucesso ID: {}", id);
    }

    public List<Paciente> buscarPorApoiador(Long apoiadorId) {
        logger.debug("Buscando pacientes por apoiador ID: {}", apoiadorId);

        if (apoiadorId == null || apoiadorId <= 0) {
            throw new ValidationException("ID do apoiador é inválido");
        }

        if (apoiadorRepository.buscarPorId(apoiadorId).isEmpty()) {
            throw new ApoiadorNotFoundException(apoiadorId);
        }

        return pacienteRepository.buscarPorApoiador(apoiadorId);
    }

    public void vincularApoiador(Long pacienteId, Long apoiadorId) {
        logger.info("Vinculando paciente {} ao apoiador {}", pacienteId, apoiadorId);

        if (pacienteId == null || apoiadorId == null) {
            throw new ValidationException("IDs não podem ser nulos");
        }

        buscarPorId(pacienteId);

        if (apoiadorRepository.buscarPorId(apoiadorId).isEmpty()) {
            throw new ApoiadorNotFoundException(apoiadorId);
        }

        boolean vinculado = apoiadorRepository.vincularPaciente(apoiadorId, pacienteId);
        if (!vinculado) {
            throw new RuntimeException("Erro ao vincular paciente ao apoiador");
        }

        logger.info("Paciente {} vinculado ao apoiador {} com sucesso", pacienteId, apoiadorId);
    }

    public void desvincularApoiador(Long pacienteId) {
        logger.info("Desvinculando paciente {}", pacienteId);

        if (pacienteId == null || pacienteId <= 0) {
            throw new ValidationException("ID do paciente é inválido");
        }

        buscarPorId(pacienteId);

        boolean desvinculado = apoiadorRepository.desvincularPaciente(pacienteId);
        if (!desvinculado) {
            throw new RuntimeException("Erro ao desvincular paciente");
        }

        logger.info("Paciente {} desvinculado com sucesso", pacienteId);
    }

    public int contarTotalPacientes() {
        return pacienteRepository.contarTotal();
    }

}