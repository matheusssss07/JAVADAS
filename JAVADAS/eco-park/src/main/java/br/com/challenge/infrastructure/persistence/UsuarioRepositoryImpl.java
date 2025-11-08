package br.com.challenge.infrastructure.persistence;

import br.com.challenge.domain.repository.UsuarioRepository;
import br.com.challenge.domain.model.Usuario;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepositoryImpl implements UsuarioRepository {

    @Inject
    AgroalDataSource dataSource;

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nome_completo";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(criarUsuarioFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários", e);
        }
        return usuarios;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarUsuarioFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM usuario WHERE cpf = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarUsuarioFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por CPF: " + cpf, e);
        }
        return Optional.empty();
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuario (id, versao, tipo_usuario, nome_completo, idade, cpf, cep, numero, complemento, telefone, senha) " +
                "VALUES (seq_usuario.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id"})) {

            String tipoUsuario = "USUARIO";
            if (usuario instanceof br.com.challenge.domain.model.Paciente) {
                tipoUsuario = "PACIENTE";
            } else if (usuario instanceof br.com.challenge.domain.model.Apoiador) {
                tipoUsuario = "APOIADOR";
            }

            stmt.setLong(1, usuario.getVersao() != null ? usuario.getVersao() : 0L);
            stmt.setString(2, tipoUsuario);
            stmt.setString(3, usuario.getNomeCompleto());
            stmt.setInt(4, usuario.getIdade());
            stmt.setString(5, usuario.getCpf());
            stmt.setString(6, usuario.getCep());
            stmt.setInt(7, usuario.getNumero());
            stmt.setString(8, usuario.getComplemento());
            stmt.setString(9, usuario.getTelefone());
            stmt.setString(10, usuario.getSenha());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                }
            }

            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

    @Override
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome_completo = ?, idade = ?, cep = ?, numero = ?, " +
                "complemento = ?, telefone = ?, senha = ?, versao = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setInt(2, usuario.getIdade());
            stmt.setString(3, usuario.getCep());
            stmt.setInt(4, usuario.getNumero());
            stmt.setString(5, usuario.getComplemento());
            stmt.setString(6, usuario.getTelefone());
            stmt.setString(7, usuario.getSenha());
            stmt.setLong(8, usuario.getVersao() != null ? usuario.getVersao() : 0L);
            stmt.setLong(9, usuario.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + usuario.getId(), e);
        }
    }

    @Override
    public boolean deletar(Long id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar usuário: " + id, e);
        }
    }

    @Override
    public boolean cpfExiste(String cpf) {
        return buscarPorCpf(cpf).isPresent();
    }

    @Override
    public boolean cpfExisteParaOutroUsuario(String cpf, Long id) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE cpf = ? AND id != ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.setLong(2, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar CPF", e);
        }
        return false;
    }

    @Override
    public List<Usuario> buscarPorFaixaEtaria(int idadeMinima, int idadeMaxima) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE idade BETWEEN ? AND ? ORDER BY idade";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idadeMinima);
            stmt.setInt(2, idadeMaxima);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(criarUsuarioFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários por faixa etária: " + idadeMinima + "-" + idadeMaxima, e);
        }
        return usuarios;
    }

    @Override
    public List<Usuario> buscarPorCep(String cep) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE cep = ? ORDER BY nome_completo";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cep);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(criarUsuarioFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários por CEP: " + cep, e);
        }
        return usuarios;
    }

    @Override
    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM usuario";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar total de usuários", e);
        }
        return 0;
    }

    private Usuario criarUsuarioFromResultSet(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getLong("id"),
                rs.getString("nome_completo"),
                rs.getInt("idade"),
                rs.getString("cpf"),
                rs.getString("cep"),
                rs.getInt("numero"),
                rs.getString("complemento"),
                rs.getString("telefone"),
                rs.getString("senha"),
                rs.getLong("versao")
        );
    }

    private void preencherUsuarioStatement(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setString(1, usuario.getNomeCompleto());
        stmt.setInt(2, usuario.getIdade());
        stmt.setString(3, usuario.getCpf());
        stmt.setString(4, usuario.getCep());
        stmt.setInt(5, usuario.getNumero());
        stmt.setString(6, usuario.getComplemento());
        stmt.setString(7, usuario.getTelefone());
        stmt.setString(8, usuario.getSenha());
    }
}