package Model.Match;

public class TurnManager implements ITurnManager{
    private boolean playerTurn;

    /**
     * Constructor - player always starts
     */
    public TurnManager() {
        this.playerTurn = true;
    }

    /**
     * Constructor with custom starting player
     *
     * @param playerStarts true if player starts, false if machine starts
     */
    public TurnManager(boolean playerStarts) {
        this.playerTurn = playerStarts;
    }

    @Override
    public boolean isPlayerTurn() {
        return playerTurn;
    }

    @Override
    public void switchTurn() {
        playerTurn = !playerTurn;
    }

    @Override
    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    @Override
    public void reset() {
        this.playerTurn = true; // Player always starts after reset
    }

    /**
     * Gets current player identifier
     *
     * @return "Player" or "Machine" depending on current turn
     */
    public String getCurrentPlayerName() {
        return playerTurn ? "Player" : "Machine";
    }

    @Override
    public String toString() {
        return "Turn: " + getCurrentPlayerName();
    }
}
