package Model.Match;

import Attacks.Attack;
import Model.Board.Board;
import Model.Player.Player;

public class Match {
    private final Player player;
    private final Player machine;
    private final ITurnManager turnManager;
    private final IGameEndValidator gameEndValidator;

    private boolean gameFinished;
    private Player winner;

    /**Constructor with dependency injection
     * @param player           The human player
     * @param machine          The AI player
     * @param turnManager      Manager for turn-taking logic
     * @param gameEndValidator Validator for game end conditions
     */
    public Match(Player player, Player machine, ITurnManager turnManager, IGameEndValidator gameEndValidator) {
        this.player = player;
        this.machine = machine;
        this.turnManager = turnManager;
        this.gameEndValidator = gameEndValidator;
        this.gameFinished = false;
        this.winner = null;
    }

    /**Simplified constructor with default implementations
     * For backward compatibility
     */
    public Match(Player player, Player machine) {
        this(player, machine, new TurnManager(), new GameEndValidator());
    }

    /**Executes an attack and manages game flow
     * Process:
     * 1. Apply attack to target board
     * 2. Check if game has ended
     * 3. Switch turns if attack missed
     *
     * @param attack   The attack to execute
     * @param attacker The attacking player
     * @param target   The target player
     * @return Result message of the attack
     */
    public String executeAttack(Attack attack, Player attacker, Player target) {
        //Apply attack to target's board
        Board targetBoard = target.getOwnBoard();
        String result = attack.apply(targetBoard);

        //Check if game has ended (delegates to validator)
        checkGameEnd();

        //If attack missed and game not finished, switch turns
        boolean isHit = result.contains("Hit");
        boolean isSunk = result.contains("Sunk");

        if (!isHit && !isSunk && !gameFinished) {
            turnManager.switchTurn();
        }

        return result;
    }

    /**Checks if the game has ended
     * Delegates to game end validator
     */
    private void checkGameEnd() {
        GameEndResult endResult = gameEndValidator.checkGameEnd(
                player.getOwnBoard(),
                machine.getOwnBoard()
        );

        if (endResult.isGameEnded()) {
            gameFinished = true;

            // Determine winner and update stats
            if (endResult.isPlayerWinner()) {
                winner = player;
                player.addWins();
            } else if (endResult.isMachineWinner()) {
                winner = machine;
                machine.addWins();
            }
        }
    }


    public boolean isPlayerTurn() {
        return turnManager.isPlayerTurn();
    }

    public void setPlayerTurn(boolean turn) {
        turnManager.setPlayerTurn(turn);
    }

    public void switchTurn() {
        turnManager.switchTurn();
    }

    //Getters
    public Player getPlayer() {
        return player;
    }

    public Player getMachine() {
        return machine;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public Player getWinner() {
        return winner;
    }

    /**Gets current active player
     */
    public Player getCurrentPlayer() {
        return turnManager.isPlayerTurn() ? player : machine;
    }

    /**Gets opponent of current player
     */
    public Player getOpponent() {
        return turnManager.isPlayerTurn() ? machine : player;
    }

    @Override
    public String toString() {
        return String.format("Match[%s vs %s, Turn: %s, Finished: %b]",
                player.getUsername(),
                machine.getUsername(),
                turnManager.isPlayerTurn() ? "Player" : "Machine",
                gameFinished
        );
    }
}

