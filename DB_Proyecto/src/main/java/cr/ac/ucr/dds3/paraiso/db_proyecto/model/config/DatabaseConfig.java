package cr.ac.ucr.dds3.paraiso.db_proyecto.model.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DatabaseConfig {

    private String url;
    private String user;
    private String password;

    public DatabaseConfig() {
    }

    public static DatabaseConfig loadFromClasspath() {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("database.properties was not found in classpath");
            }
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load database.properties", exception);
        }

        DatabaseConfig config = new DatabaseConfig();
        config.url = properties.getProperty("db.url");
        config.user = properties.getProperty("db.user");
        config.password = properties.getProperty("db.password", "");
        return config;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
