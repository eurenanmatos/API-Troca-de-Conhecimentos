package service;

import model.Usuario;
import repository.UsuarioRepository;
import security.JwtUtil;

import java.util.List;

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criar(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (usuario.getEmail() == null || !usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().length() < 4) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 4 caracteres.");
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario atualizar(Long id, Usuario dadosNovos) {
        Usuario existente = usuarioRepository.findById(id);
        if (existente == null) return null;
        existente.setNome(dadosNovos.getNome());
        existente.setEmail(dadosNovos.getEmail());
        if (dadosNovos.getHabilidadeOferecida() != null)
            existente.setHabilidadeOferecida(dadosNovos.getHabilidadeOferecida());
        if (dadosNovos.getHabilidadeDesejada() != null)
            existente.setHabilidadeDesejada(dadosNovos.getHabilidadeDesejada());
        usuarioRepository.update(existente);
        return existente;
    }

    public boolean deletar(Long id) {
        return usuarioRepository.delete(id);
    }

    /**
     * Valida email/senha e retorna um token JWT se correto.
     */
    public String login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null || !senha.equals(usuario.getSenha())) {
            return null;
        }
        return JwtUtil.gerarToken(email);
    }
}
