package Database;

import java.sql.*;

public class PlayerDAO {
    /**Verifies if the player already exists in the database by realizing a query
     * Where it searches if the username already exists
     * @param username name of the player
     */
    public boolean playerExists(String username) {
        String query = "SELECT COUNT(*) FROM players WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
        }

        return false;
    }

    /**Creates a player
     * @param username name of the player
     * @return true if there are not erros in the process of creation
     */
    public boolean createPlayer(String username) {
        String query = "INSERT INTO players (username, wins) VALUES (?, 0)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // This code is related to the violation of the unique principle
            } else {;
            }
            return false;
        }
    }

    /**
     *Obtains the victories of the player
     * @param username name of the player
     * @return number of victories
     */
    public int getPlayerWins(String username) {
        String query = "SELECT wins FROM players WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("wins");
            }

        } catch (SQLException e) {
        }

        return -1;
    }

    /**
     * Increases the player victories in one
     * @param username name of the player
     * @return true if the update is succesful
     */
    public boolean incrementWins(String username) {
        String query = "UPDATE players SET wins = wins + 1 WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Obtains or create a player
     * @param username name of the player
     * @return number of victories of the player
     */
    public int getOrCreatePlayer(String username) {
        //We verify if the player exists
        if (!playerExists(username)) {
            //If not, we createe it
            createPlayer(username);
            return 0;
        }
        //If the player exists we return its victories
        return getPlayerWins(username);
    }
}
