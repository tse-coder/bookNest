package bookNest.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Utility class for database connection
// Provides methods to connect to the MySQL database
public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root"; // Change as needed
    private static final String PASSWORD = "asdfglkjh"; // Change as needed

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
