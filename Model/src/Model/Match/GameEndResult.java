package Model.Match;

public class GameEndResult {
    private final boolean gameEnded;
    private final GameWinner winner;

    /**
     * Constructor for game end result
     *
     * @param gameEnded Whether the game has ended
     * @param winner Who won the game (PLAYER, MACHINE, or NONE)
     */
    public GameEndResult(boolean gameEnded, GameWinner winner) {
        this.gameEnded = gameEnded;
        this.winner = winner;
    }

    /**Factory method for ongoing game
     */
    public static GameEndResult gameOngoing() {
        return new GameEndResult(false, GameWinner.NONE);
    }

    /**
     * Factory method for player victory
     */
    public static GameEndResult playerWon() {
        return new GameEndResult(true, GameWinner.PLAYER);
    }

    /**
     * Factory method for machine victory
     */
    public static GameEndResult machineWon() {
        return new GameEndResult(true, GameWinner.MACHINE);
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public GameWinner getWinner() {
        return winner;
    }

    public boolean isPlayerWinner() {
        return winner == GameWinner.PLAYER;
    }

    public boolean isMachineWinner() {
        return winner == GameWinner.MACHINE;
    }

    @Override
    public String toString() {
        if (!gameEnded) {
            return "Game Ongoing";
        }
        return "Game Ended - Winner: " + winner;
    }
}

/**
 * Enum representing possible winners
 */
enum GameWinner {
    PLAYER,
    MACHINE,
    NONE  // Game not ended yet
}
