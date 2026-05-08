package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    
    // Thread-safe singleton using lazy initialization holder
    private static volatile Connection connection = null;
    private static final Object lock = new Object();

    private static Properties loadProperties() throws SQLException {
        Properties props = new Properties();
        try (java.io.InputStream input = DatabaseConnection.class.getResourceAsStream("/config/db.properties")) {
            if (input == null) {
                throw new SQLException("db.properties not found at /config/db.properties");
            }
            props.load(input);
        } catch (Exception e) {
            throw new SQLException("Failed to load db.properties", e);
        }
        return props;
    }

    /**
     * Get database connection using thread-safe singleton pattern.
     * Uses double-checked locking with synchronized block.
     * 
     * @return active database connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        // First check (without locking for performance)
        if (connection == null || connection.isClosed()) {
            synchronized (lock) {
                // Second check (after acquiring lock)
                if (connection == null || connection.isClosed()) {
                    Properties props = loadProperties();
                    String host = props.getProperty("db.host");
                    String port = props.getProperty("db.port", "3306");
                    String dbname = props.getProperty("db.name");
                    String user = props.getProperty("db.user");
                    String pass = props.getProperty("db.password");
                    
                    // Add connection pool settings for better concurrency
                    String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true&connectTimeout=10000", 
                                     host, port, dbname);
                    
                    connection = DriverManager.getConnection(url, user, pass);
                    
                    // Set connection properties for concurrency
                    try {
                        connection.setAutoCommit(true);
                        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    } catch (SQLException e) {
                        // Non-fatal, continue with default settings
                        System.out.println("Warning: Could not set connection properties - " + e.getMessage());
                    }
                }
            }
        }
        return connection;
    }

    /**
     * Close the database connection.
     * Thread-safe method to properly clean up resources.
     */
    public static void closeConnection() {
        synchronized (lock) {
            if (connection != null) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                } finally {
                    connection = null;
                }
            }
        }
    }
    
    /**
     * Check if connection is currently active.
     * 
     * @return true if connection exists and is not closed
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
