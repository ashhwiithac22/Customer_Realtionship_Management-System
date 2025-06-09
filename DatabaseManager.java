import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class DatabaseManager {
    // Make these configurable (could load from properties file or environment variables)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/crm";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "AshwithaChandru*1";

    // Connection pool would be better for production (like HikariCP)
    private static volatile Connection connection = null;

    static {
        initializeDriver();
    }

    private static void initializeDriver() {
        try {
            // Explicitly load the driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            handleInitializationError("MySQL JDBC Driver not found. Please add the connector JAR to your project.", e);
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        // In production, you'd get a connection from a pool instead
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(
                        DB_URL + "?useSSL=false&serverTimezone=UTC",
                        DB_USERNAME,
                        DB_PASSWORD
                );
            } catch (SQLException e) {
                handleConnectionError("Failed to connect to database: " + e.getMessage(), e);
                throw e;
            }
        }
        return connection;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    private static void handleInitializationError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
        showErrorDialog("Driver Error", message);
        System.exit(1); // Critical error - can't continue without driver
    }

    private static void handleConnectionError(String message, SQLException e) {
        System.err.println(message);
        e.printStackTrace();
        showErrorDialog("Database Error", message);
    }

    private static void showErrorDialog(String title, String message) {
        // Ensure this runs on the EDT (Event Dispatch Thread)
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> showErrorDialog(title, message));
            return;
        }

        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    // Utility method for executing queries safely
    public static void executeSafeQuery(String query) {
        Connection conn = null;
        try {
            conn = getConnection();
            try (var stmt = conn.createStatement()) {
                stmt.execute(query);
            }
        } catch (SQLException e) {
            handleConnectionError("Query execution failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    if (!conn.getAutoCommit()) {
                        conn.rollback();
                    }
                } catch (SQLException e) {
                    System.err.println("Rollback failed: " + e.getMessage());
                }
            }
        }
    }
}