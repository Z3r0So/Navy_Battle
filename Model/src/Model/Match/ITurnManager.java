package Model.Match;

public interface ITurnManager {
    /**Checks if it's currently the player's turn
     *
     * @return true if player's turn, false if machine's turn
     */
    boolean isPlayerTurn();

    /**Switches to the next player's turn
     */
    void switchTurn();

    /**Sets the current turn explicitly
     *
     * @param playerTurn true for player's turn, false for machine's turn
     */
    void setPlayerTurn(boolean playerTurn);
    /**Resets turn to initial state (player starts)
     */
    void reset();
}
