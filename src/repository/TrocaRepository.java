package repository;

import model.Habilidade;
import model.Troca;
import model.Usuario;
import util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrocaRepository {
    private static TrocaRepository instance;

    private TrocaRepository() {}

    public static synchronized TrocaRepository getInstance() {
        if (instance == null) instance = new TrocaRepository();
        return instance;
    }

    public Troca save(Troca troca) {
        String sql = "INSERT INTO trocas (usuario_oferecendo_id, usuario_interessado_id, habilidade_oferecida_id, habilidade_desejada_id, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setNullableLong(ps, 1, troca.getSolicitante() != null ? troca.getSolicitante().getId() : null);
            setNullableLong(ps, 2, troca.getDestinatario() != null ? troca.getDestinatario().getId() : null);
            setNullableLong(ps, 3, troca.getHabilidadeOferecida() != null ? troca.getHabilidadeOferecida().getId() : null);
            setNullableLong(ps, 4, troca.getHabilidadeDesejada() != null ? troca.getHabilidadeDesejada().getId() : null);
            ps.setString(5, troca.getStatus() != null ? troca.getStatus() : "PENDENTE");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) troca.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.err.println("TrocaRepository.save error: " + e.getMessage());
        }
        return troca;
    }

    public Troca findById(Long id) {
        String sql = "SELECT id, usuario_oferecendo_id, usuario_interessado_id, habilidade_oferecida_id, habilidade_desejada_id, status FROM trocas WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("TrocaRepository.findById error: " + e.getMessage());
        }
        return null;
    }

    public List<Troca> findAll() {
        List<Troca> result = new ArrayList<>();
        String sql = "SELECT id, usuario_oferecendo_id, usuario_interessado_id, habilidade_oferecida_id, habilidade_desejada_id, status FROM trocas";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("TrocaRepository.findAll error: " + e.getMessage());
        }
        return result;
    }

    public boolean updateStatus(Long id, String status) {
        String sql = "UPDATE trocas SET status=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TrocaRepository.updateStatus error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Long id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM trocas WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("TrocaRepository.delete error: " + e.getMessage());
        }
        return false;
    }

    private Troca mapRow(ResultSet rs) throws SQLException {
        Long solicitanteId = rs.getObject("usuario_oferecendo_id", Long.class);
        Long destinatarioId = rs.getObject("usuario_interessado_id", Long.class);
        Long ofereceId = rs.getObject("habilidade_oferecida_id", Long.class);
        Long desejaId = rs.getObject("habilidade_desejada_id", Long.class);

        Usuario solicitante = UsuarioRepository.getInstance().findById(solicitanteId);
        Usuario destinatario = UsuarioRepository.getInstance().findById(destinatarioId);
        Habilidade oferece = HabilidadeRepository.getInstance().findById(ofereceId);
        Habilidade deseja = HabilidadeRepository.getInstance().findById(desejaId);

        Troca t = new Troca(rs.getLong("id"), solicitante, destinatario, oferece, deseja);
        t.setStatus(rs.getString("status"));
        return t;
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value != null) ps.setLong(index, value);
        else ps.setNull(index, Types.INTEGER);
    }
}
