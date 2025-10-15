package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/battleship_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

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
}