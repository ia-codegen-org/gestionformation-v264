package com.gestionformation;

import com.gestionformation.config.StartupErrorListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SpringBootApplication
@EnableScheduling
public class GestionFormationApplication {

    // ✅ Requêtes constantes (nom de base figé à la génération) —
    // aucune concaténation à l'exécution, donc aucun risque
    // d'injection SQL (hotspot Sonar java:S2077).
    private static final String DB_NAME = "gestionformation";
    private static final String CHECK_DB_SQL =
            "SELECT 1 FROM pg_database WHERE datname = 'gestionformation'";
    private static final String CREATE_DB_SQL = "CREATE DATABASE \"gestionformation\"";

    public static void main(String[] args) {
        ensureDatabaseExists();
        SpringApplication app = new SpringApplication(GestionFormationApplication.class);
        // ✅ Message clair pour les échecs PostgreSQL connus (mauvais
        // mot de passe, serveur injoignable) à la place de la stack
        // trace Hibernate brute — ne change pas le comportement d'arrêt.
        app.addListeners(new StartupErrorListener());
        app.run(args);
    }

    // ✅ PostgreSQL ne crée jamais la base cible automatiquement à la
    // connexion (contrairement à MySQL avec createDatabaseIfNotExist) —
    // on la crée nous-mêmes si besoin, via la base système "postgres",
    // AVANT que Hibernate n'essaie de s'y connecter. Un échec ici ne doit
    // pas empêcher le démarrage (la base existe peut-être déjà, ou les
    // identifiants admin diffèrent) : on logue et on laisse Spring Boot
    // tenter sa propre connexion normalement.
    private static void ensureDatabaseExists() {
        String user = "postgres";
        String password = System.getenv().getOrDefault("DB_PASSWORD", "changeme");
        String adminUrl = "jdbc:postgresql://localhost:5432/postgres";

        try (Connection conn = DriverManager.getConnection(adminUrl, user, password);
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(CHECK_DB_SQL)) {
                if (!rs.next()) {
                    stmt.executeUpdate(CREATE_DB_SQL);
                    System.out.println("✅ Base '" + DB_NAME + "' créée automatiquement");
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Impossible de vérifier/créer la base '" + DB_NAME
                + "' automatiquement : " + e.getMessage()
                + " — assure-toi qu'elle existe déjà si le démarrage échoue.");
        }
    }
}
