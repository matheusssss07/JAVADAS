package br.com.challenge.infrastructure.persistence.dao;

import br.com.challenge.infrastructure.persistence.database.DatabaseConnection;
import br.com.challenge.domain.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private Connection connection;

    public UsuarioDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão no UsuarioDAO: " + e.getMessage());
        }
    }

    public void cadastrar(Usuario usuario) {
        String sql = "INSERT INTO usuario (id, nome_completo, idade, cpf, cep, numero, complemento, telefone, senha) " +
                "VALUES (seq_usuario.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"})) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setInt(2, usuario.getIdade());
            stmt.setString(3, usuario.getCpf());
            stmt.setString(4, usuario.getCep());
            stmt.setInt(5, usuario.getNumero());
            stmt.setString(6, usuario.getComplemento());
            stmt.setString(7, usuario.getTelefone());
            stmt.setString(8, usuario.getSenha());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getLong(1));
                    }
                }
            }

            System.out.println("Usuário cadastrado com ID: " + usuario.getId());

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    public Usuario buscarPorId(Long id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        Usuario usuario = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = criarUsuarioFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }

        return usuario;
    }

    public Usuario buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM usuario WHERE cpf = ?";
        Usuario usuario = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = criarUsuarioFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por CPF: " + e.getMessage());
        }

        return usuario;
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuario ORDER BY nome_completo";
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(criarUsuarioFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }

        return usuarios;
    }

    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome_completo = ?, idade = ?, cep = ?, numero = ?, " +
                "complemento = ?, telefone = ?, senha = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setInt(2, usuario.getIdade());
            stmt.setString(3, usuario.getCep());
            stmt.setInt(4, usuario.getNumero());
            stmt.setString(5, usuario.getComplemento());
            stmt.setString(6, usuario.getTelefone());
            stmt.setString(7, usuario.getSenha());
            stmt.setLong(8, usuario.getId());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Usuário atualizado: " + (rowsAffected > 0 ? "Sucesso" : "Falha"));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean deletar(Long id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Usuário deletado: " + (rowsAffected > 0 ? "Sucesso" : "Falha"));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean cpfExiste(String cpf) {
        return buscarPorCpf(cpf) != null;
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
                rs.getString("senha")
        );
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao fechar conexão: " + e.getMessage());
        }
    }
}