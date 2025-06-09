import java.sql.Connection;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        try (Connection con = DatabaseManager.getConnection()) {
            if (con != null) {
                System.out.println("Database connected successfully!");
            } else {
                System.out.println("Failed to connect to database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
