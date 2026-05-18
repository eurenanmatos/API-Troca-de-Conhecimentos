package controller;

import model.Usuario;
import repository.UsuarioRepository;
import java.util.List;

public class UsuarioController {
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}
