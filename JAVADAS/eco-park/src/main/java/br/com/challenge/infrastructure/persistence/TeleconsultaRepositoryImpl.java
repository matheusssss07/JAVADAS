package br.com.challenge.infrastructure.persistence;

import br.com.challenge.domain.repository.TeleconsultaRepository;
import br.com.challenge.domain.model.Teleconsulta;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TeleconsultaRepositoryImpl implements TeleconsultaRepository {

    @Inject
    AgroalDataSource dataSource;

    @Override
    public List<Teleconsulta> listarTodas() {
        List<Teleconsulta> teleconsultas = new ArrayList<>();
        String sql = "SELECT * FROM teleconsulta ORDER BY data_hora DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                teleconsultas.add(criarTeleconsultaFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar teleconsultas", e);
        }
        return teleconsultas;
    }

    @Override
    public Optional<Teleconsulta> buscarPorId(Long id) {
        String sql = "SELECT * FROM teleconsulta WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarTeleconsultaFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar teleconsulta por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Teleconsulta salvar(Teleconsulta teleconsulta) {
        String sql = "INSERT INTO teleconsulta (id, paciente_id, medico, data_hora, status, observacoes) " +
                "VALUES (seq_teleconsulta.NEXTVAL, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id"})) {

            stmt.setLong(1, teleconsulta.getPacienteId());
            stmt.setString(2, teleconsulta.getMedico());
            stmt.setTimestamp(3, Timestamp.valueOf(teleconsulta.getDataHora()));
            stmt.setString(4, teleconsulta.getStatus());
            stmt.setString(5, teleconsulta.getObservacoes());

            stmt.executeUpdate();

            // Obter o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    teleconsulta.setId(generatedKeys.getLong(1));
                }
            }

            return teleconsulta;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar teleconsulta", e);
        }
    }

    @Override
    public boolean atualizar(Teleconsulta teleconsulta) {
        String sql = "UPDATE teleconsulta SET paciente_id = ?, medico = ?, data_hora = ?, " +
                "status = ?, observacoes = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, teleconsulta.getPacienteId());
            stmt.setString(2, teleconsulta.getMedico());
            stmt.setTimestamp(3, Timestamp.valueOf(teleconsulta.getDataHora()));
            stmt.setString(4, teleconsulta.getStatus());
            stmt.setString(5, teleconsulta.getObservacoes());
            stmt.setLong(6, teleconsulta.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar teleconsulta: " + teleconsulta.getId(), e);
        }
    }

    @Override
    public boolean deletar(Long id) {
        String sql = "DELETE FROM teleconsulta WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar teleconsulta: " + id, e);
        }
    }

    @Override
    public List<Teleconsulta> buscarPorPaciente(Long pacienteId) {
        List<Teleconsulta> teleconsultas = new ArrayList<>();
        String sql = "SELECT * FROM teleconsulta WHERE paciente_id = ? ORDER BY data_hora DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pacienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teleconsultas.add(criarTeleconsultaFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar teleconsultas por paciente: " + pacienteId, e);
        }
        return teleconsultas;
    }

    @Override
    public List<Teleconsulta> buscarPorMedico(String medico) {
        List<Teleconsulta> teleconsultas = new ArrayList<>();
        String sql = "SELECT * FROM teleconsulta WHERE medico LIKE ? ORDER BY data_hora DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + medico + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teleconsultas.add(criarTeleconsultaFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar teleconsultas por médico: " + medico, e);
        }
        return teleconsultas;
    }

    @Override
    public List<Teleconsulta> buscarPorStatus(String status) {
        List<Teleconsulta> teleconsultas = new ArrayList<>();
        String sql = "SELECT * FROM teleconsulta WHERE status = ? ORDER BY data_hora DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teleconsultas.add(criarTeleconsultaFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar teleconsultas por status: " + status, e);
        }
        return teleconsultas;
    }

    @Override
    public List<Teleconsulta> consultasDeHoje() {
        List<Teleconsulta> teleconsultas = new ArrayList<>();
        String sql = "SELECT * FROM teleconsulta WHERE TRUNC(data_hora) = TRUNC(SYSDATE) AND status = 'AGENDADA' " +
                "ORDER BY data_hora";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                teleconsultas.add(criarTeleconsultaFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar consultas de hoje", e);
        }
        return teleconsultas;
    }

    @Override
    public boolean atualizarStatus(Long id, String novoStatus) {
        String sql = "UPDATE teleconsulta SET status = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setLong(2, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da teleconsulta: " + id, e);
        }
    }

    @Override
    public boolean adicionarObservacoes(Long id, String observacoes) {
        String sql = "UPDATE teleconsulta SET observacoes = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, observacoes);
            stmt.setLong(2, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar observações à teleconsulta: " + id, e);
        }
    }

    @Override
    public boolean horarioDisponivel(LocalDateTime dataHora, String medico) {
        String sql = "SELECT COUNT(*) FROM teleconsulta WHERE medico = ? AND data_hora BETWEEN ? AND ? AND status != 'CANCELADA'";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medico);
            stmt.setTimestamp(2, Timestamp.valueOf(dataHora));
            stmt.setTimestamp(3, Timestamp.valueOf(dataHora.plusMinutes(30)));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar disponibilidade de horário", e);
        }
        return false;
    }

    @Override
    public int contarPorStatus(String status) {
        String sql = "SELECT COUNT(*) FROM teleconsulta WHERE status = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar teleconsultas por status: " + status, e);
        }
        return 0;
    }

    @Override
    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM teleconsulta";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar teleconsultas", e);
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
}