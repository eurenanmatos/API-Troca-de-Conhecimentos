package servlet;

import controller.UsuarioController;
import model.Habilidade;
import model.Usuario;
import repository.HabilidadeRepository;
import repository.UsuarioRepository;
import service.HabilidadeService;
import service.UsuarioService;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/usuarios", "/usuarios/*"})
public class UsuarioServlet extends HttpServlet {

    private final UsuarioController usuarioController =
        new UsuarioController(new UsuarioService(UsuarioRepository.getInstance()));

    private final HabilidadeService habilidadeService =
        new HabilidadeService(HabilidadeRepository.getInstance());

    // GET /usuarios          → lista todos
    // GET /usuarios/{id}     → busca por id
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Long id = extrairId(req);
        if (id != null) {
            Usuario u = usuarioController.buscarPorId(id);
            if (u == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().print(JsonUtil.erro("Usuário não encontrado."));
            } else {
                resp.getWriter().print(JsonUtil.toJson(u));
            }
        } else {
            resp.getWriter().print(JsonUtil.toJson(usuarioController.listarUsuarios()));
        }
    }

    // POST /usuarios → cria novo usuário
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Map<String, String> dados = JsonUtil.parseJson(JsonUtil.lerCorpo(req));
            Usuario usuario = new Usuario(
                null,
                dados.get("nome"),
                dados.get("email"),
                dados.get("senha"),
                habilidadeService.buscarPorId(parseLong(dados.get("habilidadeOferecidaId"))),
                habilidadeService.buscarPorId(parseLong(dados.get("habilidadeDesejadaId")))
            );
            Usuario criado = usuarioController.criarUsuario(usuario);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().print(JsonUtil.toJson(criado));
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro(e.getMessage()));
        }
    }

    // PUT /usuarios/{id} → atualiza usuário
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Long id = extrairId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro("ID obrigatório na URL: /usuarios/{id}"));
            return;
        }
        Map<String, String> dados = JsonUtil.parseJson(JsonUtil.lerCorpo(req));
        Usuario dadosNovos = new Usuario(
            id,
            dados.get("nome"),
            dados.get("email"),
            null,
            habilidadeService.buscarPorId(parseLong(dados.get("habilidadeOferecidaId"))),
            habilidadeService.buscarPorId(parseLong(dados.get("habilidadeDesejadaId")))
        );
        Usuario atualizado = usuarioController.atualizarUsuario(id, dadosNovos);
        if (atualizado == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print(JsonUtil.erro("Usuário não encontrado."));
        } else {
            resp.getWriter().print(JsonUtil.toJson(atualizado));
        }
    }

    // DELETE /usuarios/{id} → remove usuário
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Long id = extrairId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro("ID obrigatório na URL: /usuarios/{id}"));
            return;
        }
        boolean removido = usuarioController.deletarUsuario(id);
        if (removido) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print(JsonUtil.erro("Usuário não encontrado."));
        }
    }

    private Long extrairId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("null")) return null;
        try { return Long.parseLong(value); }
        catch (NumberFormatException e) { return null; }
    }
}
