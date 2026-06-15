package repository;

import model.Usuario;
import util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {
    private static UsuarioRepository instance;

    private UsuarioRepository() {}

    public static synchronized UsuarioRepository getInstance() {
        if (instance == null) instance = new UsuarioRepository();
        return instance;
    }

    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, habilidade_oferecida_id, habilidade_desejada_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            setNullableLong(ps, 4, usuario.getHabilidadeOferecida() != null ? usuario.getHabilidadeOferecida().getId() : null);
            setNullableLong(ps, 5, usuario.getHabilidadeDesejada() != null ? usuario.getHabilidadeDesejada().getId() : null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) usuario.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.err.println("UsuarioRepository.save error: " + e.getMessage());
        }
        return usuario;
    }

    public Usuario findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT id, nome, email, senha, habilidade_oferecida_id, habilidade_desejada_id FROM usuarios WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("UsuarioRepository.findById error: " + e.getMessage());
        }
        return null;
    }

    public Usuario findByEmail(String email) {
        if (email == null) return null;
        String sql = "SELECT id, nome, email, senha, habilidade_oferecida_id, habilidade_desejada_id FROM usuarios WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("UsuarioRepository.findByEmail error: " + e.getMessage());
        }
        return null;
    }

    public List<Usuario> findAll() {
        List<Usuario> result = new ArrayList<>();
        String sql = "SELECT id, nome, email, senha, habilidade_oferecida_id, habilidade_desejada_id FROM usuarios";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("UsuarioRepository.findAll error: " + e.getMessage());
        }
        return result;
    }

    public boolean update(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome=?, email=?, habilidade_oferecida_id=?, habilidade_desejada_id=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            setNullableLong(ps, 3, usuario.getHabilidadeOferecida() != null ? usuario.getHabilidadeOferecida().getId() : null);
            setNullableLong(ps, 4, usuario.getHabilidadeDesejada() != null ? usuario.getHabilidadeDesejada().getId() : null);
            ps.setLong(5, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UsuarioRepository.update error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("UsuarioRepository.delete error: " + e.getMessage());
        }
        return false;
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario(
            rs.getLong("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("senha")
        );
        Long ofereceId = rs.getObject("habilidade_oferecida_id", Long.class);
        Long desejaId  = rs.getObject("habilidade_desejada_id", Long.class);
        if (ofereceId != null) u.setHabilidadeOferecida(HabilidadeRepository.getInstance().findById(ofereceId));
        if (desejaId  != null) u.setHabilidadeDesejada(HabilidadeRepository.getInstance().findById(desejaId));
        return u;
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value != null) ps.setLong(index, value);
        else ps.setNull(index, Types.INTEGER);
    }
}
