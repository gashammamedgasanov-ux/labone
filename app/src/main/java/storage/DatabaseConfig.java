package storage;

import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties props = new Properties();
    private static boolean loaded = false;

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                loaded = true;
                System.out.println("Database config loaded");
            } else {
                System.out.println("database.properties not found");
            }
        } catch (Exception e) {
            System.out.println("Error loading database config: " + e.getMessage());
        }
    }

    public static String getUrl() {
        return props.getProperty("db.url", "jdbc:postgresql://localhost:5432/labaone");
    }

    public static String getUser() {
        return props.getProperty("db.user", "postgres");
    }

    public static String getPassword() {
        return props.getProperty("db.password", "");
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
