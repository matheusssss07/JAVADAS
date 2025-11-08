package br.com.challenge.infrastructure.api.rest;

import br.com.challenge.domain.service.UsuarioService;
import br.com.challenge.domain.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;

@ApplicationScoped
public class UsuarioApplicationController {

    @Inject
    UsuarioService usuarioService;

    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioService.buscarPorId(id);
    }

    public Usuario buscarPorCpf(String cpf) {
        return usuarioService.buscarPorCpf(cpf);
    }

    public boolean validarLogin(String cpf, String senha) {
        return usuarioService.validarLogin(cpf, senha);
    }

    public int contarTotalUsuarios() {
        return usuarioService.contarTotalUsuarios();
    }

    public Usuario cadastrar(@Valid Usuario usuario) {
        return usuarioService.cadastrar(usuario);
    }

    public Usuario atualizar(Long id, @Valid Usuario usuario) {
        return usuarioService.atualizar(id, usuario);
    }

    public void deletar(Long id) {
        usuarioService.deletar(id);
    }

    public boolean verificarDisponibilidadeCpf(String cpf) {
        return usuarioService.verificarDisponibilidadeCpf(cpf);
    }

    public boolean cpfExiste(String cpf) {
        return !usuarioService.verificarDisponibilidadeCpf(cpf);
    }

    public boolean cpfExisteParaOutroUsuario(String cpf, Long id) {
        try {
            Usuario usuarioComCpf = usuarioService.buscarPorCpf(cpf);
            return !usuarioComCpf.getId().equals(id);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean alterarSenha(Long id, String senhaAtual, String novaSenha) {
        return usuarioService.alterarSenha(id, senhaAtual, novaSenha);
    }
}