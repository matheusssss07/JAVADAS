package br.com.challenge.infrastructure.persistence.dao;

import br.com.challenge.infrastructure.persistence.database.DatabaseConnection;
import br.com.challenge.domain.model.Teleconsulta;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeleconsultaDAO {
    private Connection connection;

    public TeleconsultaDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão no TeleconsultaDAO: " + e.getMessage());
        }
    }


    public void agendar(Teleconsulta teleconsulta) {
        String sql = "INSERT INTO teleconsulta (id, paciente_id, medico, data_hora, status, observacoes) " +
                "VALUES (seq_teleconsulta.NEXTVAL, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"})) {
            stmt.setLong(1, teleconsulta.getPacienteId());
            stmt.setString(2, teleconsulta.getMedico());
            stmt.setTimestamp(3, Timestamp.valueOf(teleconsulta.getDataHora()));
            stmt.setString(4, teleconsulta.getStatus());
            stmt.setString(5, teleconsulta.getObservacoes());

            int rowsAffected = stmt.executeUpdate();


            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        teleconsulta.setId(generatedKeys.getLong(1));
                    }
                }
            }

            System.out.println("Teleconsulta agendada com ID: " + teleconsulta.getId());

        } catch (SQLException e) {
            System.err.println("Erro ao agendar teleconsulta: " + e.getMessage());
        }
    }


    public Teleconsulta buscarPorId(Long id) {
        String sql = "SELECT * FROM teleconsulta WHERE id = ?";
        Teleconsulta teleconsulta = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    teleconsulta = criarTeleconsultaFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar teleconsulta por ID: " + e.getMessage());
        }

        return teleconsulta;
    }


    public List<Teleconsulta> listarTodas() {
        String sql = "SELECT * FROM teleconsulta ORDER BY data_hora DESC";
        List<Teleconsulta> teleconsultas = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                teleconsultas.add(criarTeleconsultaFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar teleconsultas: " + e.getMessage());
        }

        return teleconsultas;
    }


    public List<Teleconsulta> buscarPorPaciente(Long pacienteId) {
        String sql = "SELECT * FROM teleconsulta WHERE paciente_id = ? ORDER BY data_hora DESC";
        List<Teleconsulta> teleconsultas = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pacienteId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teleconsultas.add(criarTeleconsultaFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar teleconsultas por paciente: " + e.getMessage());
        }

        return teleconsultas;
    }


    public List<Teleconsulta> buscarPorMedico(String medico) {
        String sql = "SELECT * FROM teleconsulta WHERE medico LIKE ? ORDER BY data_hora DESC";
        List<Teleconsulta> teleconsultas = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + medico + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teleconsultas.add(criarTeleconsultaFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar teleconsultas por médico: " + e.getMessage());
        }

        return teleconsultas;
    }


    public List<Teleconsulta> buscarPorStatus(String status) {
        String sql = "SELECT * FROM teleconsulta WHERE status = ? ORDER BY data_hora DESC";
        List<Teleconsulta> teleconsultas = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teleconsultas.add(criarTeleconsultaFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar teleconsultas por status: " + e.getMessage());
        }

        return teleconsultas;
    }


    public boolean atualizar(Teleconsulta teleconsulta) {
        String sql = "UPDATE teleconsulta SET paciente_id = ?, medico = ?, data_hora = ?, " +
                "status = ?, observacoes = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, teleconsulta.getPacienteId());
            stmt.setString(2, teleconsulta.getMedico());
            stmt.setTimestamp(3, Timestamp.valueOf(teleconsulta.getDataHora()));
            stmt.setString(4, teleconsulta.getStatus());
            stmt.setString(5, teleconsulta.getObservacoes());
            stmt.setLong(6, teleconsulta.getId());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Teleconsulta atualizada: " + (rowsAffected > 0 ? "Sucesso" : "Falha"));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar teleconsulta: " + e.getMessage());
            return false;
        }
    }


    public boolean cancelar(Long id) {
        String sql = "DELETE FROM teleconsulta WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Teleconsulta cancelada: " + (rowsAffected > 0 ? "Sucesso" : "Falha"));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao cancelar teleconsulta: " + e.getMessage());
            return false;
        }
    }



    public boolean atualizarStatus(Long id, String novoStatus) {
        String sql = "UPDATE teleconsulta SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);
            stmt.setLong(2, id);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Status da teleconsulta atualizado para: " + novoStatus);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
            return false;
        }
    }

    // Adicionar observações à teleconsulta
    public boolean adicionarObservacoes(Long id, String observacoes) {
        String sql = "UPDATE teleconsulta SET observacoes = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, observacoes);
            stmt.setLong(2, id);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Observações adicionadas à teleconsulta");
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar observações: " + e.getMessage());
            return false;
        }
    }


    public List<Teleconsulta> consultasDeHoje() {
        String sql = "SELECT * FROM teleconsulta WHERE TRUNC(data_hora) = TRUNC(SYSDATE) AND status = 'AGENDADA' " +
                "ORDER BY data_hora";
        List<Teleconsulta> teleconsultas = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                teleconsultas.add(criarTeleconsultaFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar consultas de hoje: " + e.getMessage());
        }

        return teleconsultas;
    }

    // Verificar disponibilidade de horário
    public boolean horarioDisponivel(LocalDateTime dataHora, String medico) {
        String sql = "SELECT COUNT(*) FROM teleconsulta WHERE medico = ? AND data_hora = ? AND status != 'CANCELADA'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, medico);
            stmt.setTimestamp(2, Timestamp.valueOf(dataHora));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar disponibilidade: " + e.getMessage());
        }

        return false;
    }

    // Contar teleconsultas por status
    public int contarPorStatus(String status) {
        String sql = "SELECT COUNT(*) FROM teleconsulta WHERE status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar teleconsultas por status: " + e.getMessage());
        }

        return 0;
    }

    private Teleconsulta criarTeleconsultaFromResultSet(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("data_hora");
        LocalDateTime dataHora = timestamp != null ? timestamp.toLocalDateTime() : null;

        return new Teleconsulta(
                rs.getLong("id"),
                rs.getLong("paciente_id"),
                rs.getString("medico"),
                dataHora,
                rs.getString("status"),
                rs.getString("observacoes")
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