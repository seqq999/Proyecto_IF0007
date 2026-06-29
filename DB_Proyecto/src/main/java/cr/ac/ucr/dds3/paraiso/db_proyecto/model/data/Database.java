package cr.ac.ucr.dds3.paraiso.db_proyecto.model.data;

import cr.ac.ucr.dds3.paraiso.db_proyecto.model.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {

    private static DatabaseConfig config = DatabaseConfig.loadFromClasspath();

    private Database() {
    }

    public static void configure(DatabaseConfig databaseConfig) {
        config = databaseConfig;
        closeQuietly();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }

    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection.isValid(3);
        } catch (SQLException exception) {
            return false;
        }
    }

    public static DatabaseConfig getConfig() {
        return config;
    }

    private static void closeQuietly() {
        // Connections are short-lived per operation; nothing to close globally.
    }
}
