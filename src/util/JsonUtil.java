package util;

import model.Habilidade;
import model.Troca;
import model.Usuario;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    // -------- Serialização --------

    public static String toJson(List<?> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            if (item instanceof Usuario) sb.append(toJson((Usuario) item));
            else if (item instanceof Habilidade) sb.append(toJson((Habilidade) item));
            else if (item instanceof Troca) sb.append(toJson((Troca) item));
            else sb.append("\"").append(escape(item.toString())).append("\"");
            if (i < items.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    public static String toJson(Usuario u) {
        return "{" +
            "\"id\":" + u.getId() + "," +
            "\"nome\":\"" + escape(u.getNome()) + "\"," +
            "\"email\":\"" + escape(u.getEmail()) + "\"," +
            "\"habilidadeOferecida\":" + habilidadeJson(u.getHabilidadeOferecida()) + "," +
            "\"habilidadeDesejada\":" + habilidadeJson(u.getHabilidadeDesejada()) +
            "}";
    }

    public static String toJson(Habilidade h) {
        return "{" +
            "\"id\":" + h.getId() + "," +
            "\"nome\":\"" + escape(h.getNome()) + "\"," +
            "\"descricao\":\"" + escape(h.getDescricao() != null ? h.getDescricao() : "") + "\"" +
            "}";
    }

    public static String toJson(Troca t) {
        return "{" +
            "\"id\":" + t.getId() + "," +
            "\"solicitante\":" + (t.getSolicitante() != null ? toJson(t.getSolicitante()) : "null") + "," +
            "\"destinatario\":" + (t.getDestinatario() != null ? toJson(t.getDestinatario()) : "null") + "," +
            "\"habilidadeOferecida\":" + habilidadeJson(t.getHabilidadeOferecida()) + "," +
            "\"habilidadeDesejada\":" + habilidadeJson(t.getHabilidadeDesejada()) + "," +
            "\"status\":\"" + escape(t.getStatus()) + "\"" +
            "}";
    }

    private static String habilidadeJson(Habilidade h) {
        return h != null ? toJson(h) : "null";
    }

    public static String erro(String mensagem) {
        return "{\"erro\":\"" + escape(mensagem) + "\"}";
    }

    public static String sucesso(String mensagem) {
        return "{\"mensagem\":\"" + escape(mensagem) + "\"}";
    }

    // -------- Parsing de JSON simples (sem biblioteca externa) --------

    /**
     * Lê o corpo da requisição como texto.
     */
    public static String lerCorpo(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String linha;
            while ((linha = reader.readLine()) != null) sb.append(linha);
        }
        return sb.toString();
    }

    /**
     * Parser mínimo de JSON flat: {"chave":"valor","chave2":"valor2"}
     * Suporta valores string e numérico.
     */
    public static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        // Tokenização simples par a par
        int i = 0;
        while (i < json.length()) {
            // Pular espaços e vírgulas
            while (i < json.length() && (json.charAt(i) == ',' || json.charAt(i) == ' ')) i++;
            if (i >= json.length()) break;

            // Ler chave
            String key = null;
            if (json.charAt(i) == '"') {
                int end = json.indexOf('"', i + 1);
                if (end == -1) break;
                key = json.substring(i + 1, end);
                i = end + 1;
            }
            if (key == null) break;

            // Pular ':'
            while (i < json.length() && (json.charAt(i) == ':' || json.charAt(i) == ' ')) i++;

            // Ler valor
            String value = null;
            if (i < json.length() && json.charAt(i) == '"') {
                int end = json.indexOf('"', i + 1);
                if (end == -1) break;
                value = json.substring(i + 1, end);
                i = end + 1;
            } else {
                // Numérico ou null/true/false
                int end = i;
                while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
                value = json.substring(i, end).trim();
                i = end;
            }
            if (value != null) map.put(key, value);
        }
        return map;
    }

    private static String escape(String v) {
        if (v == null) return "";
        return v.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
