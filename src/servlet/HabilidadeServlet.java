package servlet;

import controller.HabilidadeController;
import model.Habilidade;
import repository.HabilidadeRepository;
import service.HabilidadeService;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "HabilidadeServlet", urlPatterns = {"/habilidades", "/habilidades/*"})
public class HabilidadeServlet extends HttpServlet {

    private final HabilidadeController habilidadeController =
        new HabilidadeController(new HabilidadeService(HabilidadeRepository.getInstance()));

    // GET /habilidades          → lista todas
    // GET /habilidades/{id}     → busca por id
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Long id = extrairId(req);
        if (id != null) {
            Habilidade h = habilidadeController.buscarPorId(id);
            if (h == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().print(JsonUtil.erro("Habilidade não encontrada."));
            } else {
                resp.getWriter().print(JsonUtil.toJson(h));
            }
        } else {
            resp.getWriter().print(JsonUtil.toJson(habilidadeController.listarHabilidades()));
        }
    }

    // POST /habilidades → cria nova habilidade
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Map<String, String> dados = JsonUtil.parseJson(JsonUtil.lerCorpo(req));
            Habilidade h = new Habilidade(null, dados.get("nome"), dados.get("descricao"));
            Habilidade criada = habilidadeController.criarHabilidade(h);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().print(JsonUtil.toJson(criada));
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro(e.getMessage()));
        }
    }

    // PUT /habilidades/{id} → atualiza habilidade
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Long id = extrairId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro("ID obrigatório na URL: /habilidades/{id}"));
            return;
        }
        Map<String, String> dados = JsonUtil.parseJson(JsonUtil.lerCorpo(req));
        Habilidade dadosNovos = new Habilidade(id, dados.get("nome"), dados.get("descricao"));
        Habilidade atualizada = habilidadeController.atualizarHabilidade(id, dadosNovos);
        if (atualizada == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print(JsonUtil.erro("Habilidade não encontrada."));
        } else {
            resp.getWriter().print(JsonUtil.toJson(atualizada));
        }
    }

    // DELETE /habilidades/{id}
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Long id = extrairId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro("ID obrigatório na URL: /habilidades/{id}"));
            return;
        }
        boolean removida = habilidadeController.deletarHabilidade(id);
        if (removida) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print(JsonUtil.erro("Habilidade não encontrada."));
        }
    }

    private Long extrairId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try { return Long.parseLong(pathInfo.substring(1)); }
        catch (NumberFormatException e) { return null; }
    }
}
