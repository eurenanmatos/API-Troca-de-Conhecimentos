package servlet;

import controller.HabilidadeController;
import controller.TrocaController;
import controller.UsuarioController;
import model.Troca;
import repository.HabilidadeRepository;
import repository.TrocaRepository;
import repository.UsuarioRepository;
import service.HabilidadeService;
import service.TrocaService;
import service.UsuarioService;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "TrocaServlet", urlPatterns = {"/trocas", "/trocas/*"})
public class TrocaServlet extends HttpServlet {

    private final TrocaController trocaController =
        new TrocaController(new TrocaService(TrocaRepository.getInstance()));

    private final UsuarioController usuarioController =
        new UsuarioController(new UsuarioService(UsuarioRepository.getInstance()));

    private final HabilidadeController habilidadeController =
        new HabilidadeController(new HabilidadeService(HabilidadeRepository.getInstance()));

    // GET /trocas          → lista todas
    // GET /trocas/{id}     → busca por id
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Long id = extrairId(req);
        if (id != null) {
            Troca t = trocaController.buscarPorId(id);
            if (t == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().print(JsonUtil.erro("Troca não encontrada."));
            } else {
                resp.getWriter().print(JsonUtil.toJson(t));
            }
        } else {
            resp.getWriter().print(JsonUtil.toJson(trocaController.listarTrocas()));
        }
    }

    // POST /trocas → solicita uma nova troca
    // Body: { "solicitanteId": 1, "destinatarioId": 2, "habilidadeOferecidaId": 1, "habilidadeDesejadaId": 2 }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Map<String, String> dados = JsonUtil.parseJson(JsonUtil.lerCorpo(req));
            Troca troca = new Troca(
                null,
                usuarioController.buscarPorId(parseLong(dados.get("solicitanteId"))),
                usuarioController.buscarPorId(parseLong(dados.get("destinatarioId"))),
                habilidadeController.buscarPorId(parseLong(dados.get("habilidadeOferecidaId"))),
                habilidadeController.buscarPorId(parseLong(dados.get("habilidadeDesejadaId")))
            );
            Troca criada = trocaController.solicitarTroca(troca);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().print(JsonUtil.toJson(criada));
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro(e.getMessage()));
        }
    }

    // PUT /trocas/{id} → atualiza o status da troca
    // Body: { "status": "ACEITA" }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Long id = extrairId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro("ID obrigatório na URL: /trocas/{id}"));
            return;
        }
        Map<String, String> dados = JsonUtil.parseJson(JsonUtil.lerCorpo(req));
        String novoStatus = dados.get("status");
        if (novoStatus == null || novoStatus.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro("Campo 'status' é obrigatório."));
            return;
        }
        Troca atualizada = trocaController.atualizarStatus(id, novoStatus);
        if (atualizada == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print(JsonUtil.erro("Troca não encontrada."));
        } else {
            resp.getWriter().print(JsonUtil.toJson(atualizada));
        }
    }

    // DELETE /trocas/{id}
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Long id = extrairId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro("ID obrigatório na URL: /trocas/{id}"));
            return;
        }
        boolean removida = trocaController.deletarTroca(id);
        if (removida) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print(JsonUtil.erro("Troca não encontrada."));
        }
    }

    private Long extrairId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try { return Long.parseLong(pathInfo.substring(1)); }
        catch (NumberFormatException e) { return null; }
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("null")) return null;
        try { return Long.parseLong(value); }
        catch (NumberFormatException e) { return null; }
    }
}
