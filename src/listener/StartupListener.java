package listener;

import controller.HabilidadeController;
import controller.UsuarioController;
import model.Habilidade;
import model.Usuario;
import repository.HabilidadeRepository;
import repository.UsuarioRepository;
import service.HabilidadeService;
import service.UsuarioService;
import util.Database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Database.init();
        seedData();
        System.out.println("✅ API Troca de Conhecimentos inicializada com sucesso!");
    }

    private void seedData() {
        HabilidadeController habilidadeController =
            new HabilidadeController(new HabilidadeService(HabilidadeRepository.getInstance()));
        UsuarioController usuarioController =
            new UsuarioController(new UsuarioService(UsuarioRepository.getInstance()));

        Habilidade java = habilidadeController.buscarPorNome("Java");
        if (java == null)
            java = habilidadeController.criarHabilidade(new Habilidade(null, "Java", "Programação em Java"));

        Habilidade sql = habilidadeController.buscarPorNome("SQL");
        if (sql == null)
            sql = habilidadeController.criarHabilidade(new Habilidade(null, "SQL", "Consultas em banco de dados"));

        if (usuarioController.listarUsuarios().isEmpty()) {
            try {
                usuarioController.criarUsuario(new Usuario(null, "Renan", "renan@example.com", "1234", java, sql));
                usuarioController.criarUsuario(new Usuario(null, "Ana",   "ana@example.com",   "1234", sql,  java));
            } catch (Exception e) {
                System.err.println("Seed: " + e.getMessage());
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
