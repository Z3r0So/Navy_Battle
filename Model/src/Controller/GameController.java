package Controller;

import Attacks.Attack;
import Model.Boat.*;
import Model.Match.Match;
import Model.Player.HumanPlayer;
import Model.Player.Machine;
import Model.Player.Player;

import java.util.Random;

public class GameController {
    private Match currentMatch;
    private Random random;

    public GameController() {
        this.random = new Random();
    }

    /**
     * Start a new game with a human player and a machine player
     * @param playerName Name of the human player
     * @param password Password of the human player
     * @return true if the game started successfully, false otherwise
     */
    public boolean startNewGame(String playerName, String password) {
        try {
            Player humanPlayer = new HumanPlayer(playerName, password);
            Player machinePlayer = new Machine();

            currentMatch = new Match(humanPlayer, machinePlayer);

            //Place ships automatically for both players
            boolean playerShipsPlaced = placeShipsAutomatically(humanPlayer);
            boolean machineShipsPlaced = placeShipsAutomatically(machinePlayer);

            if (!playerShipsPlaced || !machineShipsPlaced) {
                currentMatch = null;
                return false;
            }

            return true;
        } catch (Exception e) {
            currentMatch = null;
            return false;
        }
    }

    /**
     * Place ships automatically on the player's board
     * @param player Player on whose board the ships will be placed
     * @return true if all ships were placed successfully, false otherwise
     */
    private boolean placeShipsAutomatically(Player player) {
        // Flota estándar de batalla naval
        Boat[] fleet = {
                new Aircrafter(),
                new Cruise(),
                new Cruise(),
                new Destructor(),
                new Destructor(),
                new Submarine(),
                new Submarine(),
                new Submarine()
        };

        for (Boat boat : fleet) {
            boolean placed = false;
            int attempts = 0;
            int maxAttempts = 100;

            while (!placed && attempts < maxAttempts) {
                int row = random.nextInt(10);
                int column = random.nextInt(10);
                boolean horizontal = random.nextBoolean();

                placed = player.getOwnBoard().placeShip(boat, row, column, horizontal);
                attempts++;
            }

            if (!placed) {
                return false;
            }
        }

        return true;
    }

    /**
     * Executes the player's attack
     * @param row Row to attack
     * @param column Column to attack
     * @return Result of the attack
     */
    public String playerAttack(int row, int column) {
        if (currentMatch == null) {
            return "No game in progress!";
        }

        if (!currentMatch.isPlayerTurn()) {
            return "Not your turn!";
        }

        if (currentMatch.isGameFinished()) {
            return "Game already finished!";
        }

        try {

            HumanPlayer human = (HumanPlayer) currentMatch.getPlayer();
            human.setNextAttack(row, column);

            Attack attack = human.makeAttack(currentMatch.getMachine().getOwnBoard());
            String result = currentMatch.executeAttack(attack,
                    currentMatch.getPlayer(),
                    currentMatch.getMachine());

            return result;

        } catch (IllegalArgumentException | IllegalStateException e) {
            return "Invalid attack: " + e.getMessage();
        }
    }

    /**
     * Executes the machine's attack
     * @return Result of the attack
     */
    public String machineAttack() {
        if (currentMatch == null) {
            return "No game in progress!";
        }

        if (currentMatch.isPlayerTurn()) {
            return "Not machine's turn!";
        }

        if (currentMatch.isGameFinished()) {
            return "Game already finished!";
        }

        try {
            Machine machine = (Machine) currentMatch.getMachine();

            Attack attack = machine.makeAttack(currentMatch.getPlayer().getOwnBoard());
            String result = currentMatch.executeAttack(attack,
                    currentMatch.getMachine(),
                    currentMatch.getPlayer());
            // Notify the machine of the attack result for learning purposes of AI
            machine.notifyAttackResult(attack.getRow(),
                    attack.getColumn(),
                    result,
                    currentMatch.getPlayer().getOwnBoard());

            return result;

        } catch (Exception e) {
            return "Machine attack error: " + e.getMessage();
        }
    }

    /**
     *Places a ship for the player at the specified position
     * @param boat Boat to place
     * @param row Starting row
     * @param column Starting column
     * @param horizontal True if the boat is placed horizontally, false if vertically
     * @return true if the ship was placed successfully, false otherwise
     */
    public boolean placePlayerShip(Boat boat, int row, int column, boolean horizontal) {
        if (currentMatch == null) {
            return false;
        }
        return currentMatch.getPlayer().getOwnBoard().placeShip(boat, row, column, horizontal);
    }

    /**
     * Reinicia los tableros del jugador (útil para recolocar barcos)
     */
    public void resetPlayerBoard() {
        if (currentMatch != null) {
            currentMatch.getPlayer().getOwnBoard().initialize();
        }
    }

    /**
     * The method checks if it's the player's turn
     */
    public boolean isPlayerTurn() {
        return currentMatch != null && currentMatch.isPlayerTurn();
    }

    /**
     * Verify if the game has finished
     */
    public boolean isGameFinished() {
        return currentMatch != null && currentMatch.isGameFinished();
    }

    /**
     * Obtains the winner of the game
     * @return
     */
    public Player getWinner() {
        return currentMatch != null ? currentMatch.getWinner() : null;
    }

    /**
     * Obtains the current match
     * @return Current match
     */
    public Match getCurrentMatch() {
        return currentMatch;
    }

    /**
     * Restart the game, resetting both players' boards
     */
    public void resetGame() {
        if (currentMatch != null) {
            currentMatch.getPlayer().resetBoards();
            currentMatch.getMachine().resetBoards();
        }
        currentMatch = null;
    }

    /**
     * Obtains the state of the player's own board
     * @return Matrix with the state of the board
     */
    public int[][] getPlayerOwnBoardState() {
        if (currentMatch == null) return new int[10][10];
        return currentMatch.getPlayer().getOwnBoard().getBoardState();
    }

    /**
     * Obtains the state of the player's attack board (or enemy board)
     * @return Matrix with the state of the attack board (or enemy board)
     */
    public int[][] getPlayerAttackBoardState() {
        if (currentMatch == null) return new int[10][10];
        return currentMatch.getPlayer().getAttackBoard().getBoardState();
    }
}
