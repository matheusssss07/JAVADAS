package br.com.challenge.infrastructure.persistence.dao;

import br.com.challenge.infrastructure.persistence.database.DatabaseConnection;
import br.com.challenge.domain.model.Apoiador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApoiadorDAO {
    private Connection connection;

    public ApoiadorDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão no ApoiadorDAO: " + e.getMessage());
        }
    }


    public void cadastrar(Apoiador apoiador) {

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.cadastrar(apoiador);


        String sql = "INSERT INTO apoiador (id, cargo, area_atuacao) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, apoiador.getId());
            stmt.setString(2, apoiador.getCargo());
            stmt.setString(3, apoiador.getAreaAtuacao());

            stmt.executeUpdate();
            System.out.println("Apoiador cadastrado com ID: " + apoiador.getId());

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar apoiador: " + e.getMessage());
        } finally {
            usuarioDAO.close();
        }
    }


    public Apoiador buscarPorId(Long id) {
        String sql = "SELECT u.*, a.cargo, a.area_atuacao " +
                "FROM usuario u INNER JOIN apoiador a ON u.id = a.id " +
                "WHERE u.id = ?";
        Apoiador apoiador = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    apoiador = criarApoiadorFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar apoiador por ID: " + e.getMessage());
        }

        return apoiador;
    }


    public Apoiador buscarPorCpf(String cpf) {
        String sql = "SELECT u.*, a.cargo, a.area_atuacao " +
                "FROM usuario u INNER JOIN apoiador a ON u.id = a.id " +
                "WHERE u.cpf = ?";
        Apoiador apoiador = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    apoiador = criarApoiadorFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar apoiador por CPF: " + e.getMessage());
        }

        return apoiador;
    }


    public List<Apoiador> listarTodos() {
        String sql = "SELECT u.*, a.cargo, a.area_atuacao " +
                "FROM usuario u INNER JOIN apoiador a ON u.id = a.id " +
                "ORDER BY u.nome_completo";
        List<Apoiador> apoiadores = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                apoiadores.add(criarApoiadorFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar apoiadores: " + e.getMessage());
        }

        return apoiadores;
    }


    public List<Apoiador> buscarPorCargo(String cargo) {
        String sql = "SELECT u.*, a.cargo, a.area_atuacao " +
                "FROM usuario u INNER JOIN apoiador a ON u.id = a.id " +
                "WHERE a.cargo = ? ORDER BY u.nome_completo";
        List<Apoiador> apoiadores = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cargo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apoiadores.add(criarApoiadorFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar apoiadores por cargo: " + e.getMessage());
        }

        return apoiadores;
    }


    public boolean atualizar(Apoiador apoiador) {

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        boolean usuarioAtualizado = usuarioDAO.atualizar(apoiador);

        if (!usuarioAtualizado) {
            return false;
        }


        String sql = "UPDATE apoiador SET cargo = ?, area_atuacao = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, apoiador.getCargo());
            stmt.setString(2, apoiador.getAreaAtuacao());
            stmt.setLong(3, apoiador.getId());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Apoiador atualizado: " + (rowsAffected > 0 ? "Sucesso" : "Falha"));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println(" Erro ao atualizar apoiador: " + e.getMessage());
            return false;
        } finally {
            usuarioDAO.close();
        }
    }


    public boolean deletar(Long id) {

        if (temPacientesVinculados(id)) {
            System.err.println("Não é possível deletar apoiador com pacientes vinculados!");
            return false;
        }

        String sqlApoiador = "DELETE FROM apoiador WHERE id = ?";
        String sqlUsuario = "DELETE FROM usuario WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtApoiador = connection.prepareStatement(sqlApoiador)) {
                stmtApoiador.setLong(1, id);
                stmtApoiador.executeUpdate();
            }

            try (PreparedStatement stmtUsuario = connection.prepareStatement(sqlUsuario)) {
                stmtUsuario.setLong(1, id);
                int rowsAffected = stmtUsuario.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit();
                    System.out.println("Apoiador deletado com sucesso!");
                    return true;
                }
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erro no rollback: " + ex.getMessage());
            }
            System.err.println("Erro ao deletar apoiador: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erro ao restaurar auto-commit: " + e.getMessage());
            }
        }

        return false;
    }


    private boolean temPacientesVinculados(Long apoiadorId) {
        String sql = "SELECT COUNT(*) FROM paciente WHERE apoiador_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, apoiadorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar pacientes vinculados: " + e.getMessage());
        }

        return false;
    }


    public int contarTotalApoiadores() {
        String sql = "SELECT COUNT(*) FROM apoiador";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar apoiadores: " + e.getMessage());
        }

        return 0;
    }


    public List<String> listarCargos() {
        String sql = "SELECT DISTINCT cargo FROM apoiador ORDER BY cargo";
        List<String> cargos = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cargos.add(rs.getString("cargo"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar cargos: " + e.getMessage());
        }

        return cargos;
    }


    public boolean vincularPaciente(Long apoiadorId, Long pacienteId) {
        String sql = "UPDATE paciente SET apoiador_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, apoiadorId);
            stmt.setLong(2, pacienteId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Paciente vinculado ao apoiador: " + (rowsAffected > 0 ? "Sucesso" : "Falha"));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao vincular paciente: " + e.getMessage());
            return false;
        }
    }


    private Apoiador criarApoiadorFromResultSet(ResultSet rs) throws SQLException {
        return new Apoiador(
                rs.getLong("id"),
                rs.getString("nome_completo"),
                rs.getInt("idade"),
                rs.getString("cpf"),
                rs.getString("cep"),
                rs.getInt("numero"),
                rs.getString("complemento"),
                rs.getString("telefone"),
                rs.getString("senha"),
                rs.getString("cargo"),
                rs.getString("area_atuacao")
        );
    }


    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}