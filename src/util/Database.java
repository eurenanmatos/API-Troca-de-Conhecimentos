package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String HOST_URL = env("DB_HOST_URL",
            "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC");
    private static final String DB_URL   = env("DB_URL",
            "jdbc:mysql://localhost:3306/banco2?useSSL=false&serverTimezone=UTC");
    private static final String USER     = env("DB_USER", "root");
    private static final String PASS     = env("DB_PASS", "");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver não encontrado. Adicione o Connector/J ao classpath.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void init() {
        // 1. Cria o banco se não existir
        try (Connection conn = DriverManager.getConnection(HOST_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS banco2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        } catch (SQLException e) {
            System.err.println("Não foi possível criar o banco banco2: " + e.getMessage());
        }

        // 2. Cria as tabelas
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS habilidades (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nome VARCHAR(100) NOT NULL," +
                "  descricao VARCHAR(255) DEFAULT ''" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nome VARCHAR(100) NOT NULL," +
                "  email VARCHAR(100) UNIQUE NOT NULL," +
                "  senha VARCHAR(255) NOT NULL DEFAULT ''," +
                "  habilidade_oferecida_id INT," +
                "  habilidade_desejada_id INT," +
                "  FOREIGN KEY (habilidade_oferecida_id) REFERENCES habilidades(id)," +
                "  FOREIGN KEY (habilidade_desejada_id) REFERENCES habilidades(id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS trocas (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  usuario_oferecendo_id INT," +
                "  usuario_interessado_id INT," +
                "  habilidade_oferecida_id INT," +
                "  habilidade_desejada_id INT," +
                "  status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE'," +
                "  FOREIGN KEY (usuario_oferecendo_id) REFERENCES usuarios(id)," +
                "  FOREIGN KEY (usuario_interessado_id) REFERENCES usuarios(id)," +
                "  FOREIGN KEY (habilidade_oferecida_id) REFERENCES habilidades(id)," +
                "  FOREIGN KEY (habilidade_desejada_id) REFERENCES habilidades(id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            // Migrações seguras para banco existente
            tryAlter(stmt, "ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS senha VARCHAR(255) NOT NULL DEFAULT ''");
            tryAlter(stmt, "ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS habilidade_oferecida_id INT");
            tryAlter(stmt, "ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS habilidade_desejada_id INT");
            tryAlter(stmt, "ALTER TABLE trocas ADD COLUMN IF NOT EXISTS habilidade_oferecida_id INT");
            tryAlter(stmt, "ALTER TABLE trocas ADD COLUMN IF NOT EXISTS habilidade_desejada_id INT");
            tryAlter(stmt, "ALTER TABLE trocas ADD COLUMN IF NOT EXISTS status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE'");

        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }

    private static void tryAlter(Statement stmt, String sql) {
        try { stmt.executeUpdate(sql); }
        catch (SQLException e) { /* coluna já existe — ignorar */ }
    }

    private static String env(String key, String defaultValue) {
        String v = System.getenv(key);
        return (v != null && !v.isEmpty()) ? v : defaultValue;
    }
}
