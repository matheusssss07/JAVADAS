package br.com.challenge.domain.service;

import br.com.challenge.domain.exceptions.UsuarioNotFoundException;
import br.com.challenge.domain.exceptions.ValidationException;
import br.com.challenge.domain.exceptions.ConcurrentModificationException;
import br.com.challenge.domain.logging.Logger;
import br.com.challenge.domain.model.Usuario;
import br.com.challenge.domain.repository.UsuarioRepository;
import br.com.challenge.infrastructure.logging.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Inject
    UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        logger.info("Listando todos os usuários");
        return usuarioRepository.listarTodos();
    }

    public Usuario buscarPorId(Long id) {
        logger.debug("Buscando usuário por ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Tentativa de buscar usuário com ID inválido: {}", id);
            throw new ValidationException("ID do usuário é inválido");
        }

        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado com ID: {}", id);
                    return new UsuarioNotFoundException(id);
                });
    }

    public Usuario buscarPorCpf(String cpf) {
        logger.debug("Buscando usuário por CPF: {}", cpf);

        if (cpf == null || cpf.trim().isEmpty()) {
            logger.warn("Tentativa de buscar usuário com CPF vazio");
            throw new ValidationException("CPF não pode ser vazio");
        }

        return usuarioRepository.buscarPorCpf(cpf)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado com CPF: {}", cpf);
                    return new UsuarioNotFoundException(cpf);
                });
    }

    //public Usuario cadastrar(@Valid Usuario usuario) {
    //    logger.info("Tentativa de cadastro de usuário: {}", usuario.getCpf());
//
    //    if (usuarioRepository.cpfExiste(usuario.getCpf())) {
    //        logger.warn("CPF já cadastrado: {}", usuario.getCpf());
    //        throw new ValidationException("CPF já cadastrado no sistema");
    //    }
//
    //    if (usuario.getIdade() < 0) {
    //        logger.warn("Idade inválida: {}", usuario.getIdade());
    //        throw new ValidationException("Idade não pode ser negativa");
    //    }
//
    //    if (usuario.getIdade() < 18) {
    //        logger.warn("Usuário menor de idade: {}", usuario.getIdade());
    //        throw new ValidationException("Usuário deve ter pelo menos 18 anos");
    //    }
//
    //    Usuario salvo = usuarioRepository.salvar(usuario);
    //    logger.info("Usuário cadastrado com ID: {} e versão: {}", salvo.getId(), salvo.getVersao());
    //    return salvo;
    //}

    public Usuario atualizar(Long id, @Valid Usuario usuario) {
        logger.info("Tentativa de atualização do usuário ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Tentativa de atualizar usuário com ID inválido: {}", id);
            throw new ValidationException("ID do usuário é inválido");
        }

        Usuario existente = buscarPorId(id);

        if (usuarioRepository.cpfExisteParaOutroUsuario(usuario.getCpf(), id)) {
            logger.warn("CPF {} já cadastrado para outro usuário", usuario.getCpf());
            throw new ValidationException("CPF já cadastrado para outro usuário");
        }

        if (usuario.getIdade() < 18) {
            logger.warn("Usuário menor de idade na atualização: {}", usuario.getIdade());
            throw new ValidationException("Usuário deve ter pelo menos 18 anos");
        }

        usuario.setId(id);
        usuario.setVersao(existente.getVersao() + 1);

        boolean atualizado = usuarioRepository.atualizar(usuario);

        if (!atualizado) {
            logger.error("Falha ao atualizar usuário ID: {} - Versão conflitante", id);
            throw new ConcurrentModificationException("Usuário", id, existente.getVersao());
        }

        logger.info("Usuário atualizado com sucesso ID: {}, nova versão: {}", id, usuario.getVersao());
        return usuario;
    }

    public void deletar(Long id) {
        logger.info("Tentativa de exclusão do usuário ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Tentativa de deletar usuário com ID inválido: {}", id);
            throw new ValidationException("ID do usuário é inválido");
        }

        buscarPorId(id);

        boolean deletado = usuarioRepository.deletar(id);
        if (!deletado) {
            logger.error("Falha ao deletar usuário ID: {}", id);
            throw new RuntimeException("Erro ao deletar usuário");
        }

        logger.info("Usuário deletado com sucesso ID: {}", id);
    }

    public boolean validarLogin(String cpf, String senha) {
        logger.debug("Validando login para CPF: {}", cpf);

        if (cpf == null || senha == null) {
            logger.warn("Tentativa de login com credenciais nulas");
            return false;
        }

        Optional<Usuario> usuario = usuarioRepository.buscarPorCpf(cpf);

        if (usuario.isPresent()) {
            boolean senhaValida = usuario.get().getSenha().equals(senha);
            if (senhaValida) {
                logger.info("Login válido para CPF: {}", cpf);
            } else {
                logger.warn("Senha inválida para CPF: {}", cpf);
            }
            return senhaValida;
        } else {
            logger.warn("Usuário não encontrado para CPF: {}", cpf);
            return false;
        }
    }

    public boolean alterarSenha(Long id, String senhaAtual, String novaSenha) {
        logger.info("Tentativa de alteração de senha para usuário ID: {}", id);

        if (id == null || id <= 0) {
            throw new ValidationException("ID do usuário é inválido");
        }

        if (novaSenha == null || novaSenha.length() < 6) {
            throw new ValidationException("Nova senha deve ter no mínimo 6 caracteres");
        }

        Usuario usuario = buscarPorId(id);

        if (!usuario.getSenha().equals(senhaAtual)) {
            logger.warn("Senha atual incorreta para usuário ID: {}", id);
            throw new ValidationException("Senha atual incorreta");
        }

        usuario.setSenha(novaSenha);
        usuario.incrementarVersao();

        boolean senhaAlterada = usuarioRepository.atualizar(usuario);

        if (!senhaAlterada) {
            logger.error("Falha ao alterar senha para usuário ID: {} - Versão conflitante", id);
            throw new ConcurrentModificationException("Usuário", id, usuario.getVersao() - 1);
        }

        logger.info("Senha alterada com sucesso para usuário ID: {}", id);
        return true;
    }

    public int contarTotalUsuarios() {
        int total = usuarioRepository.listarTodos().size();
        logger.debug("Total de usuários no sistema: {}", total);
        return total;
    }

    public boolean verificarDisponibilidadeCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        return !usuarioRepository.cpfExiste(cpf);
    }
















    public Usuario cadastrar(@Valid Usuario usuario) {
        logger.info("Tentativa de cadastro de usuário: {}", usuario.getCpf());

        // DEBUG
        System.out.println("=== DEBUG USUARIO SERVICE ===");
        System.out.println("Usuario CPF: " + usuario.getCpf());
        System.out.println("Usuario Nome: " + usuario.getNomeCompleto());
        System.out.println("Usuario Idade: " + usuario.getIdade());

        if (usuarioRepository.cpfExiste(usuario.getCpf())) {
            logger.warn("CPF já cadastrado: {}", usuario.getCpf());
            throw new ValidationException("CPF já cadastrado no sistema");
        }

        try {
            Usuario salvo = usuarioRepository.salvar(usuario);
            System.out.println("=== SUCESSO USUARIO SERVICE ===");
            System.out.println("Usuario salvo com ID: " + salvo.getId());
            logger.info("Usuário cadastrado com ID: {} e versão: {}", salvo.getId(), salvo.getVersao());
            return salvo;
        } catch (Exception e) {
            System.out.println("=== ERRO NO USUARIO SERVICE ===");
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

}