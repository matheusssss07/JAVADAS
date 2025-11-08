package br.com.challenge.infrastructure.persistence.dao;

import br.com.challenge.infrastructure.persistence.database.DatabaseConnection;
import br.com.challenge.domain.model.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {
    private Connection connection;

    public PacienteDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão no PacienteDAO: " + e.getMessage());
        }
    }

    public void cadastrar(Paciente paciente) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);


            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioDAO.cadastrar(paciente);

            if (paciente.getId() == null) {
                throw new SQLException("Falha ao obter ID do usuário");
            }


            String sql = "INSERT INTO paciente (id, telefone_contato, apoiador_id) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, paciente.getId());
                stmt.setString(2, paciente.getTelefoneContato());

                if (paciente.getApoiadorId() != null) {
                    stmt.setLong(3, paciente.getApoiadorId());
                } else {
                    stmt.setNull(3, Types.NUMERIC);
                }

                stmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Paciente cadastrado com ID: " + paciente.getId());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro no rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erro ao cadastrar paciente: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    public Paciente buscarPorId(Long id) {
        String sql = "SELECT u.*, p.telefone_contato, p.apoiador_id " +
                "FROM usuario u INNER JOIN paciente p ON u.id = p.id " +
                "WHERE u.id = ?";
        Paciente paciente = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = criarPacienteFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar paciente por ID: " + e.getMessage());
        }

        return paciente;
    }

    public Paciente buscarPorCpf(String cpf) {
        String sql = "SELECT u.*, p.telefone_contato, p.apoiador_id " +
                "FROM usuario u INNER JOIN paciente p ON u.id = p.id " +
                "WHERE u.cpf = ?";
        Paciente paciente = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = criarPacienteFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar paciente por CPF: " + e.getMessage());
        }

        return paciente;
    }

    public List<Paciente> listarTodos() {
        String sql = "SELECT u.*, p.telefone_contato, p.apoiador_id " +
                "FROM usuario u INNER JOIN paciente p ON u.id = p.id " +
                "ORDER BY u.nome_completo";
        List<Paciente> pacientes = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pacientes.add(criarPacienteFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar pacientes: " + e.getMessage());
        }

        return pacientes;
    }

    public List<Paciente> buscarPorApoiador(Long apoiadorId) {
        String sql = "SELECT u.*, p.telefone_contato, p.apoiador_id " +
                "FROM usuario u INNER JOIN paciente p ON u.id = p.id " +
                "WHERE p.apoiador_id = ? ORDER BY u.nome_completo";
        List<Paciente> pacientes = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, apoiadorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pacientes.add(criarPacienteFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar pacientes por apoiador: " + e.getMessage());
        }

        return pacientes;
    }

    public boolean atualizar(Paciente paciente) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);


            UsuarioDAO usuarioDAO = new UsuarioDAO();
            boolean usuarioAtualizado = usuarioDAO.atualizar(paciente);

            if (!usuarioAtualizado) {
                conn.rollback();
                return false;
            }


            String sql = "UPDATE paciente SET telefone_contato = ?, apoiador_id = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, paciente.getTelefoneContato());

                if (paciente.getApoiadorId() != null) {
                    stmt.setLong(2, paciente.getApoiadorId());
                } else {
                    stmt.setNull(2, Types.NUMERIC);
                }

                stmt.setLong(3, paciente.getId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            System.out.println("Paciente atualizado com sucesso!");
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro no rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erro ao atualizar paciente: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    public boolean deletar(Long id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);


            String sqlPaciente = "DELETE FROM paciente WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPaciente)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            String sqlUsuario = "DELETE FROM usuario WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUsuario)) {
                stmt.setLong(1, id);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    System.out.println("Paciente deletado com sucesso!");
                    return true;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro no rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erro ao deletar paciente: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    public int contarTotalPacientes() {
        String sql = "SELECT COUNT(*) FROM paciente";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar pacientes: " + e.getMessage());
        }

        return 0;
    }

    private Paciente criarPacienteFromResultSet(ResultSet rs) throws SQLException {
        Paciente paciente = new Paciente(
                rs.getLong("id"),
                rs.getString("nome_completo"),
                rs.getInt("idade"),
                rs.getString("cpf"),
                rs.getString("cep"),
                rs.getInt("numero"),
                rs.getString("complemento"),
                rs.getString("telefone"),
                rs.getString("senha"),
                rs.getString("telefone_contato"),
                rs.getLong("apoiador_id")
        );

        if (rs.getObject("apoiador_id") == null) {
            paciente.setApoiadorId(null);
        }

        return paciente;
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