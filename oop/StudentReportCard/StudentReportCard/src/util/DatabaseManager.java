package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/student_report_system";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "Pisi2020";

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            System.out.println("Database connection successful!");
            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
            throw new SQLException("Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Connection failed!");
            System.err.println("URL: " + DB_URL);
            System.err.println("User: " + DB_USER);
            e.printStackTrace();
            throw e;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection!");
                e.printStackTrace();
            }
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed!");
            e.printStackTrace();
            return false;
        }
    }
}