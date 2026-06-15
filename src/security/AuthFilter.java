package security;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/usuarios/*", "/habilidades/*", "/trocas/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Libera requisições OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(resp, "Acesso não autorizado: token não enviado.");
            return;
        }

        String token = authHeader.substring(7);

        if (!JwtUtil.validarToken(token)) {
            sendUnauthorized(resp, "Acesso não autorizado: token inválido ou expirado.");
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse resp, String mensagem) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().print("{\"erro\": \"" + mensagem + "\"}");
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
