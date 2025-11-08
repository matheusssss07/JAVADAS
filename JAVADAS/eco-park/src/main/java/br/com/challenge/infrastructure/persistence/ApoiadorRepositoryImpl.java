package br.com.challenge.infrastructure.persistence;

import br.com.challenge.domain.repository.ApoiadorRepository;
import br.com.challenge.domain.repository.UsuarioRepository;
import br.com.challenge.domain.model.Apoiador;
import br.com.challenge.domain.model.Usuario;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ApoiadorRepositoryImpl implements ApoiadorRepository {

    @Inject
    AgroalDataSource dataSource;

    @Inject
    UsuarioRepository usuarioRepository;

    @Override
    public List<Apoiador> listarTodos() {
        List<Apoiador> apoiadores = new ArrayList<>();
        String sql = "SELECT u.*, a.cargo, a.area_atuacao " +
                "FROM usuario u INNER JOIN apoiador a ON u.id = a.id " +
                "ORDER BY u.nome_completo";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                apoiadores.add(criarApoiadorFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar apoiadores", e);
        }
        return apoiadores;
    }

    @Override
    public Optional<Apoiador> buscarPorId(Long id) {
        String sql = "SELECT u.*, a.cargo, a.area_atuacao " +
                "FROM usuario u INNER JOIN apoiador a ON u.id = a.id " +
                "WHERE u.id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarApoiadorFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar apoiador por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Apoiador> buscarPorCpf(String cpf) {
        String sql = "SELECT u.*, a.cargo, a.area_atuacao " +
                "FROM usuario u INNER JOIN apoiador a ON u.id = a.id " +
                "WHERE u.cpf = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(criarApoiadorFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar apoiador por CPF: " + cpf, e);
        }
        return Optional.empty();
    }

    @Override
    public Apoiador salvar(Apoiador apoiador) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // 1. Salvar como Usuario primeiro (ISSO GERA O ID) - CORRIGIDO
            Usuario usuarioSalvo = usuarioRepository.salvar(apoiador); // CAPTURE O RETORNO
            apoiador.setId(usuarioSalvo.getId()); // ATUALIZE O ID DO APOIADOR

            // 2. Salvar dados específicos do Apoiador
            String sqlApoiador = "INSERT INTO apoiador (id, cargo, area_atuacao) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlApoiador)) {
                stmt.setLong(1, apoiador.getId()); // AGORA O ID ESTÁ CORRETO
                stmt.setString(2, apoiador.getCargo());
                stmt.setString(3, apoiador.getAreaAtuacao());
                stmt.executeUpdate();
            }

            conn.commit();
            return apoiador;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log do erro de rollback
                }
            }
            throw new RuntimeException("Erro ao salvar apoiador", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log do erro de fechamento
                }
            }
        }
    }

    @Override
    public boolean atualizar(Apoiador apoiador) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // 1. Atualizar dados de Usuario
            boolean usuarioAtualizado = usuarioRepository.atualizar(apoiador);
            if (!usuarioAtualizado) {
                conn.rollback();
                return false;
            }

            // 2. Atualizar dados específicos do Apoiador
            String sqlApoiador = "UPDATE apoiador SET cargo = ?, area_atuacao = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlApoiador)) {
                stmt.setString(1, apoiador.getCargo());
                stmt.setString(2, apoiador.getAreaAtuacao());
                stmt.setLong(3, apoiador.getId());

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
                    // Log do erro de rollback
                }
            }
            throw new RuntimeException("Erro ao atualizar apoiador: " + apoiador.getId(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log do erro de fechamento
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

            // 1. Deletar da tabela apoiador
            String sqlApoiador = "DELETE FROM apoiador WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlApoiador)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            // 2. Deletar da tabela usuario
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
                    // Log do erro de rollback
                }
            }
            throw new RuntimeException("Erro ao deletar apoiador: " + id, e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log do erro de fechamento
                }
            }
        }
    }

    @Override
    public List<Apoiador> buscarPorCargo(String cargo) {
        List<Apoiador> apoiadores = new ArrayList<>();
        String sql = "SELECT u.*, a.cargo, a.area_atuacao " +
                "FROM usuario u INNER JOIN apoiador a ON u.id = a.id " +
                "WHERE a.cargo = ? ORDER BY u.nome_completo";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cargo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apoiadores.add(criarApoiadorFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar apoiadores por cargo: " + cargo, e);
        }
        return apoiadores;
    }

    @Override
    public List<String> listarCargos() {
        List<String> cargos = new ArrayList<>();
        String sql = "SELECT DISTINCT cargo FROM apoiador ORDER BY cargo";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cargos.add(rs.getString("cargo"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cargos", e);
        }
        return cargos;
    }

    @Override
    public boolean vincularPaciente(Long apoiadorId, Long pacienteId) {
        String sql = "UPDATE paciente SET apoiador_id = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, apoiadorId);
            stmt.setLong(2, pacienteId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular paciente ao apoiador", e);
        }
    }

    @Override
    public boolean desvincularPaciente(Long pacienteId) {
        String sql = "UPDATE paciente SET apoiador_id = NULL WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pacienteId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desvincular paciente", e);
        }
    }

    @Override
    public boolean temPacientesVinculados(Long apoiadorId) {
        String sql = "SELECT COUNT(*) FROM paciente WHERE apoiador_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, apoiadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar pacientes vinculados", e);
        }
        return false;
    }

    @Override
    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM apoiador";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar apoiadores", e);
        }
        return 0;
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
}