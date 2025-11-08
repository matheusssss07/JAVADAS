package br.com.challenge.domain.repository;

import br.com.challenge.domain.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    List<Usuario> listarTodos();
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorCpf(String cpf);
    Usuario salvar(Usuario usuario);
    boolean atualizar(Usuario usuario);
    boolean deletar(Long id);

    boolean cpfExiste(String cpf);
    boolean cpfExisteParaOutroUsuario(String cpf, Long id);


    List<Usuario> buscarPorFaixaEtaria(int idadeMinima, int idadeMaxima);
    List<Usuario> buscarPorCep(String cep);
    int contarTotal();
}