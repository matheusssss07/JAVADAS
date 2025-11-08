package br.com.challenge.infrastructure.persistence;

import br.com.challenge.domain.repository.PacienteRepository;
import br.com.challenge.domain.repository.UsuarioRepository;
import br.com.challenge.domain.model.Paciente;
import br.com.challenge.domain.model.Usuario;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PacienteRepositoryImpl implements PacienteRepository {

    @Inject
    AgroalDataSource dataSource;

    @Inject
    UsuarioRepository usuarioRepository;

    @Override
    public List<Paciente> listarTodos() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT u.*, p.telefone_contato, p.numero_sus_ou_convenio, p.apoiador_id " +
                "FROM usuario u INNER JOIN paciente p ON u.id = p.id " +
                "ORDER BY u.nome_completo";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pacientes.add(criarPacienteFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pacientes", e);
        }
        return pacientes;
    }

    @Override
    public Optional<Paciente> buscarPorId(Long id) {
        String sql = "SELECT u.*, p.telefone_contato, p.numero_sus_ou_convenio, p.apoiador_id " +
                "FROM usuario u INNER JOIN paciente p ON u.id = p.id " +
                "WHERE u.id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarPacienteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Paciente> buscarPorCpf(String cpf) {
        String sql = "SELECT u.*, p.telefone_contato, p.numero_sus_ou_convenio, p.apoiador_id " +
                "FROM usuario u INNER JOIN paciente p ON u.id = p.id " +
                "WHERE u.cpf = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarPacienteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente por CPF: " + cpf, e);
        }
        return Optional.empty();
    }

    @Override
    public Paciente salvar(Paciente paciente) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            Usuario usuarioSalvo = usuarioRepository.salvar(paciente);
            paciente.setId(usuarioSalvo.getId());

            String sqlPaciente = "INSERT INTO paciente (id, telefone_contato, numero_sus_ou_convenio, apoiador_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPaciente)) {
                stmt.setLong(1, paciente.getId());
                stmt.setString(2, paciente.getTelefoneContato());
                stmt.setString(3, paciente.getNumeroSusOuConvenio()); // CORRIGIDO: getNumeroSusOuConvenio()

                if (paciente.getApoiadorId() != null) {
                    stmt.setLong(4, paciente.getApoiadorId());
                } else {
                    stmt.setNull(4, Types.NUMERIC);
                }
                stmt.executeUpdate();
            }

            conn.commit();
            return paciente;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new RuntimeException("Erro ao salvar paciente", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public boolean atualizar(Paciente paciente) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            boolean usuarioAtualizado = usuarioRepository.atualizar(paciente);
            if (!usuarioAtualizado) {
                conn.rollback();
                return false;
            }

            String sqlPaciente = "UPDATE paciente SET telefone_contato = ?, numero_sus_ou_convenio = ?, apoiador_id = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPaciente)) {
                stmt.setString(1, paciente.getTelefoneContato());
                stmt.setString(2, paciente.getNumeroSusOuConvenio()); // CORRIGIDO: getNumeroSusOuConvenio()

                if (paciente.getApoiadorId() != null) {
                    stmt.setLong(3, paciente.getApoiadorId());
                } else {
                    stmt.setNull(3, Types.NUMERIC);
                }
                stmt.setLong(4, paciente.getId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new RuntimeException("Erro ao atualizar paciente: " + paciente.getId(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public boolean deletar(Long id) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            String sqlPaciente = "DELETE FROM paciente WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPaciente)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            boolean usuarioDeletado = usuarioRepository.deletar(id);
            if (!usuarioDeletado) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new RuntimeException("Erro ao deletar paciente: " + id, e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public List<Paciente> buscarPorApoiador(Long apoiadorId) {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT u.*, p.telefone_contato, p.numero_sus_ou_convenio, p.apoiador_id " +
                "FROM usuario u INNER JOIN paciente p ON u.id = p.id " +
                "WHERE p.apoiador_id = ? ORDER BY u.nome_completo";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, apoiadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pacientes.add(criarPacienteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pacientes por apoiador: " + apoiadorId, e);
        }
        return pacientes;
    }

    @Override
    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM paciente";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar pacientes", e);
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
                rs.getLong("apoiador_id"),
                rs.getString("numero_sus_ou_convenio"), // NOVO CAMPO
                rs.getLong("versao")
        );

        if (rs.getObject("apoiador_id") == null) {
            paciente.setApoiadorId(null);
        }

        return paciente;
    }
}