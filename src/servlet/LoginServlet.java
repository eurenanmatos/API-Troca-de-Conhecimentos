package servlet;

import controller.UsuarioController;
import repository.UsuarioRepository;
import service.UsuarioService;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UsuarioController usuarioController =
        new UsuarioController(new UsuarioService(UsuarioRepository.getInstance()));

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json; charset=UTF-8");

        String body = JsonUtil.lerCorpo(req);
        Map<String, String> dados = JsonUtil.parseJson(body);

        String email = dados.get("email");
        String senha = dados.get("senha");

        if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print(JsonUtil.erro("E-mail e senha são obrigatórios."));
            return;
        }

        String token = usuarioController.login(email, senha);

        if (token == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().print(JsonUtil.erro("Acesso não autorizado: e-mail ou senha inválidos."));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print("{\"token\":\"" + token + "\"}");
    }
}
