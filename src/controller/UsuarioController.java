package controller;

import model.Usuario;
import service.UsuarioService;

import java.util.List;

public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario criarUsuario(Usuario usuario) {
        return usuarioService.criar(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioService.buscarPorId(id);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioService.listarTodos();
    }

    public Usuario atualizarUsuario(Long id, Usuario dadosNovos) {
        return usuarioService.atualizar(id, dadosNovos);
    }

    public boolean deletarUsuario(Long id) {
        return usuarioService.deletar(id);
    }

    public String login(String email, String senha) {
        return usuarioService.login(email, senha);
    }
}
