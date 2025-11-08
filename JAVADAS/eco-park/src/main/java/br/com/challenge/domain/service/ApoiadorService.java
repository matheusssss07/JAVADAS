package br.com.challenge.domain.service;

import br.com.challenge.domain.exceptions.ApoiadorNotFoundException;
import br.com.challenge.domain.exceptions.ConcurrentModificationException;
import br.com.challenge.domain.exceptions.ValidationException;
import br.com.challenge.domain.exceptions.PacienteNotFoundException;
import br.com.challenge.domain.logging.Logger;
import br.com.challenge.domain.model.Apoiador;
import br.com.challenge.domain.repository.ApoiadorRepository;
import br.com.challenge.domain.repository.PacienteRepository;
import br.com.challenge.infrastructure.logging.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;

@ApplicationScoped
public class ApoiadorService {

    private static final Logger logger = LoggerFactory.getLogger(ApoiadorService.class);

    @Inject
    ApoiadorRepository apoiadorRepository;

    @Inject
    PacienteRepository pacienteRepository;

    public List<Apoiador> listarTodos() {
        logger.info("Listando todos os apoiadores");
        return apoiadorRepository.listarTodos();
    }

    public Apoiador buscarPorId(Long id) {
        logger.debug("Buscando apoiador por ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Tentativa de buscar apoiador com ID inválido: {}", id);
            throw new ValidationException("ID do apoiador é inválido");
        }

        return apoiadorRepository.buscarPorId(id)
                .orElseThrow(() -> {
                    logger.warn("Apoiador não encontrado com ID: {}", id);
                    return new ApoiadorNotFoundException(id);
                });
    }

    public Apoiador buscarPorCpf(String cpf) {
        logger.debug("Buscando apoiador por CPF: {}", cpf);

        if (cpf == null || cpf.trim().isEmpty()) {
            logger.warn("Tentativa de buscar apoiador com CPF vazio");
            throw new ValidationException("CPF não pode ser vazio");
        }

        return apoiadorRepository.buscarPorCpf(cpf)
                .orElseThrow(() -> {
                    logger.warn("Apoiador não encontrado com CPF: {}", cpf);
                    return new ApoiadorNotFoundException(0L);
                });
    }

    public Apoiador cadastrar(@Valid Apoiador apoiador) {

        System.out.println("=== DEBUG APOIADOR SERVICE ===");
        System.out.println("Service CPF: " + apoiador.getCpf());
        System.out.println("Service Nome: " + apoiador.getNomeCompleto());
        System.out.println("Service Idade: " + apoiador.getIdade());
        System.out.println("Service Cargo: " + apoiador.getCargo());
        System.out.println("Service Area: " + apoiador.getAreaAtuacao());
        System.out.println("Service Completo: " + apoiador.toString());

        System.out.println("Tentativa de cadastro de apoiador: " + apoiador.getCpf());

        if (apoiadorRepository.buscarPorCpf(apoiador.getCpf()).isPresent()) {
            System.out.println("CPF já cadastrado: " + apoiador.getCpf());
            throw new ValidationException("CPF já cadastrado no sistema");
        }

        try {
            Apoiador salvo = apoiadorRepository.salvar(apoiador);

            System.out.println("=== SUCESSO APOIADOR SERVICE ===");
            System.out.println("Apoiador salvo com ID: " + salvo.getId());
            System.out.println("Apoiador salvo versão: " + salvo.getVersao());

            System.out.println("Apoiador cadastrado com ID: " + salvo.getId() + " e versão: " + salvo.getVersao());

            return salvo;
        } catch (Exception e) {
            System.out.println("=== ERRO NO APOIADOR SERVICE ===");
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar apoiador: " + e.getMessage(), e);
        }
    }

    public Apoiador atualizar(Long id, @Valid Apoiador apoiador) {
        System.out.println("Tentativa de atualização do apoiador ID: " + id);

        if (id == null || id <= 0) {
            System.out.println("Tentativa de atualizar apoiador com ID inválido: " + id);
            throw new ValidationException("ID do apoiador é inválido");
        }

        Apoiador existente = buscarPorId(id);

        apoiadorRepository.buscarPorCpf(apoiador.getCpf())
                .ifPresent(a -> {
                    if (!a.getId().equals(id)) {
                        System.out.println("CPF " + apoiador.getCpf() + " já cadastrado para outro apoiador");
                        throw new ValidationException("CPF já cadastrado para outro apoiador");
                    }
                });

        apoiador.setId(id);
        apoiador.setVersao(existente.getVersao() + 1);

        boolean atualizado = apoiadorRepository.atualizar(apoiador);

        if (!atualizado) {
            System.out.println("Falha ao atualizar apoiador ID: " + id + " - Versão conflitante");
            throw new ConcurrentModificationException("Apoiador", id, existente.getVersao());
        }

        System.out.println("Apoiador atualizado com sucesso ID: " + id + ", nova versão: " + apoiador.getVersao());
        return apoiador;
    }

    public void deletar(Long id) {
        System.out.println("Tentativa de exclusão do apoiador ID: " + id);

        if (id == null || id <= 0) {
            System.out.println("Tentativa de deletar apoiador com ID inválido: " + id);
            throw new ValidationException("ID do apoiador é inválido");
        }

        buscarPorId(id);

        if (apoiadorRepository.temPacientesVinculados(id)) {
            System.out.println("Tentativa de deletar apoiador ID: " + id + " com pacientes vinculados");
            throw new ValidationException("Não é possível deletar apoiador com pacientes vinculados");
        }

        boolean deletado = apoiadorRepository.deletar(id);
        if (!deletado) {
            System.out.println("Falha ao deletar apoiador ID: " + id);
            throw new RuntimeException("Erro ao deletar apoiador");
        }

        System.out.println("Apoiador deletado com sucesso ID: " + id);
    }

    public List<Apoiador> buscarPorCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new ValidationException("Cargo não pode ser vazio");
        }
        return apoiadorRepository.buscarPorCargo(cargo);
    }

    public List<String> listarCargos() {
        return apoiadorRepository.listarCargos();
    }

    public void vincularPaciente(Long apoiadorId, Long pacienteId) {
        if (apoiadorId == null || pacienteId == null) {
            throw new ValidationException("IDs não podem ser nulos");
        }

        buscarPorId(apoiadorId);

        if (pacienteRepository.buscarPorId(pacienteId).isEmpty()) {
            throw new PacienteNotFoundException(pacienteId);
        }

        boolean vinculado = apoiadorRepository.vincularPaciente(apoiadorId, pacienteId);
        if (!vinculado) {
            throw new RuntimeException("Erro ao vincular paciente");
        }

        System.out.println("Paciente " + pacienteId + " vinculado ao apoiador " + apoiadorId + " com sucesso");
    }

    public void desvincularPaciente(Long pacienteId) {
        if (pacienteId == null || pacienteId <= 0) {
            throw new ValidationException("ID do paciente é inválido");
        }

        if (pacienteRepository.buscarPorId(pacienteId).isEmpty()) {
            throw new PacienteNotFoundException(pacienteId);
        }

        boolean desvinculado = apoiadorRepository.desvincularPaciente(pacienteId);
        if (!desvinculado) {
            throw new RuntimeException("Erro ao desvincular paciente");
        }

        System.out.println("Paciente " + pacienteId + " desvinculado com sucesso");
    }

    public int contarTotalApoiadores() {
        return apoiadorRepository.contarTotal();
    }

    public int contarPacientesVinculados(Long apoiadorId) {
        if (apoiadorId == null || apoiadorId <= 0) {
            throw new ValidationException("ID do apoiador é inválido");
        }

        buscarPorId(apoiadorId);
        return pacienteRepository.buscarPorApoiador(apoiadorId).size();
    }
}