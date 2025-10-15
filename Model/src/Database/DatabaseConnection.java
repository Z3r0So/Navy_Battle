package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/Proyecto";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Pikachu001*";

    private static Connection connection = null;

    /**This method establishes a connection to the PostgreSQL database.
     * If the connection is already established, it returns the existing one.
     * If not, it attempts to create a new connection.
     * In case of failure, it prints an error message with possible causes.
     * @return Connection object if successful, null otherwise
    * */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connection established successfully.");
            }
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**Closes the connection to the database
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
        }
    }

    /**Tests the database connection
     * @return true if the connection is successful, false otherwise
     */
    public static boolean testConnection() {
        Connection conn = getConnection();
        return conn != null;
    }
    /**Initialize database if it does not exist
    * */
    public static void initialize() {
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.err.println("Cannot initialize database - no connection");
                return;
            }

            String createTableSQL =
                    "CREATE TABLE IF NOT EXISTS players (" +
                            "    id SERIAL PRIMARY KEY," +
                            "    username VARCHAR(50) UNIQUE NOT NULL," +
                            "    wins INTEGER DEFAULT 0," +
                            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")";

            Statement stmt = conn.createStatement();
            stmt.execute(createTableSQL);
            stmt.close();

            System.out.println("Database initialized - table 'players' ready");

        } catch (SQLException e) {
            System.err.println("Error initializing database:");
            e.printStackTrace();
        }
    }
}