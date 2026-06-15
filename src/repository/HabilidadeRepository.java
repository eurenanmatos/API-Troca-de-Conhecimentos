package repository;

import model.Habilidade;
import util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabilidadeRepository {
    private static HabilidadeRepository instance;

    private HabilidadeRepository() {}

    public static synchronized HabilidadeRepository getInstance() {
        if (instance == null) instance = new HabilidadeRepository();
        return instance;
    }

    public Habilidade save(Habilidade habilidade) {
        String sql = "INSERT INTO habilidades (nome, descricao) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, habilidade.getNome());
            ps.setString(2, habilidade.getDescricao() != null ? habilidade.getDescricao() : "");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) habilidade.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.err.println("HabilidadeRepository.save error: " + e.getMessage());
        }
        return habilidade;
    }

    public Habilidade findById(Long id) {
        if (id == null) return null;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, nome, descricao FROM habilidades WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("HabilidadeRepository.findById error: " + e.getMessage());
        }
        return null;
    }

    public Habilidade findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) return null;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, nome, descricao FROM habilidades WHERE nome = ?")) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("HabilidadeRepository.findByNome error: " + e.getMessage());
        }
        return null;
    }

    public List<Habilidade> findAll() {
        List<Habilidade> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, nome, descricao FROM habilidades");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("HabilidadeRepository.findAll error: " + e.getMessage());
        }
        return result;
    }

    public boolean update(Habilidade habilidade) {
        String sql = "UPDATE habilidades SET nome=?, descricao=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, habilidade.getNome());
            ps.setString(2, habilidade.getDescricao() != null ? habilidade.getDescricao() : "");
            ps.setLong(3, habilidade.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("HabilidadeRepository.update error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Long id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM habilidades WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("HabilidadeRepository.delete error: " + e.getMessage());
        }
        return false;
    }

    private Habilidade mapRow(ResultSet rs) throws SQLException {
        return new Habilidade(rs.getLong("id"), rs.getString("nome"), rs.getString("descricao"));
    }
}
