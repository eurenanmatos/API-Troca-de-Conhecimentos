package view;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controller.HabilidadeController;
import controller.TrocaController;
import controller.UsuarioController;
import model.Habilidade;
import model.Troca;
import model.Usuario;
import repository.HabilidadeRepository;
import repository.TrocaRepository;
import repository.UsuarioRepository;
import service.TrocaService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        HabilidadeRepository habilidadeRepository = new HabilidadeRepository();
        TrocaRepository trocaRepository = new TrocaRepository();

        UsuarioController usuarioController = new UsuarioController(usuarioRepository);
        HabilidadeController habilidadeController = new HabilidadeController(habilidadeRepository);
        TrocaService trocaService = new TrocaService(trocaRepository);
        TrocaController trocaController = new TrocaController(trocaService);

        Habilidade java = habilidadeController.criarHabilidade(new Habilidade(1L, "Java", "Programação em Java"));
        Habilidade sql = habilidadeController.criarHabilidade(new Habilidade(2L, "SQL", "Consultas em banco de dados"));

        usuarioController.criarUsuario(new Usuario(1L, "Renan", java, sql));
        usuarioController.criarUsuario(new Usuario(2L, "Ana", sql, java));

        startServer(usuarioController, habilidadeController, trocaController);
    }

    private static void startServer(UsuarioController usuarioController,
                                    HabilidadeController habilidadeController,
                                    TrocaController trocaController) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> sendResponse(exchange, 200, getHomePage(), "text/html; charset=UTF-8"));
        server.createContext("/usuarios", exchange -> sendResponse(exchange, 200, toJson(usuarioController.listarUsuarios()), "application/json; charset=UTF-8"));
        server.createContext("/habilidades", exchange -> sendResponse(exchange, 200, toJson(habilidadeController.listarHabilidades()), "application/json; charset=UTF-8"));
        server.createContext("/trocas", exchange -> sendResponse(exchange, 200, toJson(trocaController.listarTrocas()), "application/json; charset=UTF-8"));
        server.createContext("/nova-troca", new NewExchangeHandler(usuarioController, trocaController));

        server.setExecutor(null);
        server.start();

        System.out.println("Servidor iniciado: http://localhost:8080");
    }

    private static String getHomePage() {
        return "<html>" +
               "<head><meta charset=\"UTF-8\"><title>Troca de Conhecimentos</title></head>" +
               "<body>" +
               "<h1>Troca de Conhecimentos</h1>" +
               "<p>API básica para listar usuários, habilidades e trocas.</p>" +
               "<ul>" +
               "<li><a href=\"/usuarios\">/usuarios</a></li>" +
               "<li><a href=\"/habilidades\">/habilidades</a></li>" +
               "<li><a href=\"/trocas\">/trocas</a></li>" +
               "</ul>" +
               "<p>Para criar uma troca use:</p>" +
               "<pre>/nova-troca?solicitante=1&destinatario=2&oferecida=1&desejada=2</pre>" +
               "</body></html>";
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String toJson(List<?> items) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            if (item instanceof Usuario) {
                builder.append(toJson((Usuario) item));
            } else if (item instanceof Habilidade) {
                builder.append(toJson((Habilidade) item));
            } else if (item instanceof Troca) {
                builder.append(toJson((Troca) item));
            } else {
                builder.append("\"").append(item.toString()).append("\"");
            }
            if (i < items.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private static String toJson(Usuario usuario) {
        return "{" +
               "\"id\":" + usuario.getId() + "," +
               "\"nome\":\"" + escapeJson(usuario.getNome()) + "\"," +
               "\"oferece\":\"" + escapeJson(getHabilidadeNome(usuario.getHabilidadeOferecida())) + "\"," +
               "\"deseja\":\"" + escapeJson(getHabilidadeNome(usuario.getHabilidadeDesejada())) + "\"" +
               "}";
    }

    private static String toJson(Habilidade habilidade) {
        return "{" +
               "\"id\":" + habilidade.getId() + "," +
               "\"nome\":\"" + escapeJson(habilidade.getNome()) + "\"," +
               "\"descricao\":\"" + escapeJson(habilidade.getDescricao()) + "\"" +
               "}";
    }

    private static String toJson(Troca troca) {
        return "{" +
               "\"id\":" + troca.getId() + "," +
               "\"solicitante\":\"" + escapeJson(troca.getSolicitante().getNome()) + "\"," +
               "\"destinatario\":\"" + escapeJson(troca.getDestinatario().getNome()) + "\"," +
               "\"oferecida\":\"" + escapeJson(getHabilidadeNome(troca.getHabilidadeOferecida())) + "\"," +
               "\"desejada\":\"" + escapeJson(getHabilidadeNome(troca.getHabilidadeDesejada())) + "\"," +
               "\"status\":\"" + escapeJson(troca.getStatus()) + "\"" +
               "}";
    }

    private static String getHabilidadeNome(Habilidade habilidade) {
        return habilidade != null ? habilidade.getNome() : "";
    }

    private static String escapeJson(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return result;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=", 2);
            String key = urlDecode(parts[0]);
            String value = parts.length > 1 ? urlDecode(parts[1]) : "";
            result.put(key, value);
        }
        return result;
    }

    private static String urlDecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 encoding not supported", e);
        }
    }

    private static class NewExchangeHandler implements HttpHandler {
        private final UsuarioController usuarioController;
        private final TrocaController trocaController;

        public NewExchangeHandler(UsuarioController usuarioController, TrocaController trocaController) {
            this.usuarioController = usuarioController;
            this.trocaController = trocaController;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"erro\": \"Método não permitido\"}", "application/json; charset=UTF-8");
                return;
            }

            URI requestUri = exchange.getRequestURI();
            Map<String, String> params = parseQuery(requestUri.getQuery());
            Long solicitanteId = parseLong(params.get("solicitante"));
            Long destinatarioId = parseLong(params.get("destinatario"));
            Long oferecidaId = parseLong(params.get("oferecida"));
            Long desejadaId = parseLong(params.get("desejada"));

            Usuario solicitante = usuarioController.buscarPorId(solicitanteId);
            Usuario destinatario = usuarioController.buscarPorId(destinatarioId);
            Habilidade oferecida = null;
            Habilidade desejada = null;

            if (solicitante != null && solicitante.getHabilidadeOferecida() != null && solicitante.getHabilidadeOferecida().getId().equals(oferecidaId)) {
                oferecida = solicitante.getHabilidadeOferecida();
            }
            if (destinatario != null && destinatario.getHabilidadeOferecida() != null && destinatario.getHabilidadeOferecida().getId().equals(desejadaId)) {
                desejada = destinatario.getHabilidadeOferecida();
            }

            if (solicitante == null || destinatario == null || oferecida == null || desejada == null) {
                sendResponse(exchange, 400, "{\"erro\": \"Parâmetros inválidos ou usuário/habilidade não encontrado\"}", "application/json; charset=UTF-8");
                return;
            }

            Troca troca = new Troca(System.currentTimeMillis(), solicitante, destinatario, oferecida, desejada);
            Troca resultado = trocaController.solicitarTroca(troca);
            sendResponse(exchange, 200, toJson(resultado), "application/json; charset=UTF-8");
        }

        private Long parseLong(String value) {
            try {
                return value == null ? null : Long.parseLong(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
