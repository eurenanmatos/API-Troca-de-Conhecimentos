package repository;

import model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {
    private final List<Usuario> usuarios = new ArrayList<>();

    public Usuario save(Usuario usuario) {
        usuarios.add(usuario);
        return usuario;
    }

    public Usuario findById(Long id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Usuario> findAll() {
        return new ArrayList<>(usuarios);
    }
}
