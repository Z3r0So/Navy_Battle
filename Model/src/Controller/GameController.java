package Controller;

import Attacks.Attack;
import Attacks.CrossBombAttack;
import Attacks.NukeAttack;
import Attacks.TorpedoAttack;
import Controller.Interfaces.IAttackController;
import Controller.Interfaces.IBoardController;
import Controller.Interfaces.IGameLifecycle;
import Controller.Interfaces.ITurnController;
import Model.Boat.*;
import Model.Match.Match;
import Model.Player.HumanPlayer;
import Services.IAttackService;
import Services.AttackService;
import Model.Player.Machine;
import Model.Player.Player;
import Services.IShipPlacementService;
import Services.ShipPlacementService;

import java.util.Random;

public class GameController implements IGameLifecycle,
        IAttackController,
        IBoardController,
        ITurnController {
    private Match currentMatch;
    private Random random;
    private final IAttackService attackService;
    private final IShipPlacementService shipPlacementService;

    public GameController() {
        this.attackService = new AttackService();
        this.shipPlacementService = new ShipPlacementService();
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


            // Use ship placement service (S - Single Responsibility)
            Boat[] fleet = createStandardFleet();
            boolean playerShipsPlaced = shipPlacementService.placeFleetAutomatically(
                    humanPlayer.getOwnBoard(), fleet
            );

            Boat[] machineFleet = createStandardFleet();
            boolean machineShipsPlaced = shipPlacementService.placeFleetAutomatically(
                    machinePlayer.getOwnBoard(), machineFleet
            );


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

    /** Creates a standard fleet of boats
     * @return Array of boats representing the standard fleet
     */
    private Boat[] createStandardFleet() {
        return new Boat[] {
                new Aircrafter(),
                new Cruise(),
                new Cruise(),
                new Destructor(),
                new Destructor(),
                new Submarine(),
                new Submarine(),
                new Submarine()
        };
    }


    /**
     * Executes the player's attack
     * @param row Row to attack
     * @param column Column to attack
     * @return Result of the attack
     */
    public String playerAttack(int row, int column) {
        if (!validateGameState()) {
            return "No game in progress!";
        }
        try {
            HumanPlayer human = (HumanPlayer) currentMatch.getPlayer();
            human.setNextAttack(row, column);

            Attack attack = human.makeAttack(currentMatch.getMachine().getOwnBoard());
            String result = currentMatch.executeAttack(
                    attack,
                    currentMatch.getPlayer(),
                    currentMatch.getMachine()
            );
            boolean wasHit = result.contains("Hit") || result.contains("Sunk");
            attackService.markAttackOnBoard(
                    human.getAttackBoard(),
                    row,
                    column,
                    wasHit
            );
            return result;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "Invalid attack: " + e.getMessage();
        }

    }
    @Override
    public String playerCrossBombAttack(int row, int column) {
        if (!validateGameState()) {
            return "Invalid game state!";
        }

        HumanPlayer human = (HumanPlayer) currentMatch.getPlayer();

        if (!human.getPowerUps().hasCrossBombs()) {
            return "No cross bombs available!";
        }

        human.getPowerUps().useCrossBomb();

        Attack attack = new CrossBombAttack(row, column);
        String result = currentMatch.executeAttack(attack, human, currentMatch.getMachine());

        // Use attack service to mark pattern (S - Single Responsibility)
        attackService.markCrossAttackPattern(
                human.getAttackBoard(),
                currentMatch.getMachine().getOwnBoard(),
                row,
                column
        );

        return result;
    }
    @Override
    public String playerTorpedoAttack(int row, int column, boolean isHorizontal) {
        if (!validateGameState()) {
            return "Invalid game state!";
        }
        HumanPlayer human = (HumanPlayer) currentMatch.getPlayer();
        if (!human.getPowerUps().hasTorpedoes()) {
            return "No torpedoes available!";
        }
        human.getPowerUps().useTorpedo();
        Attack attack = new TorpedoAttack(row, column, isHorizontal);
        String result = currentMatch.executeAttack(attack, human, currentMatch.getMachine());

        // Use attack service to mark pattern (S - Single Responsibility)
        attackService.markTorpedoAttackPattern(
                human.getAttackBoard(),
                currentMatch.getMachine().getOwnBoard(),
                row,
                column,
                isHorizontal
        );

        return result;

    }
    /** Method to execute a nuke attack
     * @param row Row to attack
     * @param column Column to attack
     * @return Result of the attack
    * */
    @Override
    public String playerNukeAttack(int row, int column) {
        if (!validateGameState()) {
            return "Invalid game state!";
        }

        HumanPlayer human = (HumanPlayer) currentMatch.getPlayer();

        if (!human.getPowerUps().hasNukes()) {
            return "No nukes available!";
        }

        human.getPowerUps().useNuke();

        Attack attack = new NukeAttack(row, column);
        String result = currentMatch.executeAttack(attack, human, currentMatch.getMachine());

        // Use attack service to mark pattern (S - Single Responsibility)
        attackService.markNukeAttackPattern(
                human.getAttackBoard(),
                currentMatch.getMachine().getOwnBoard(),
                row,
                column
        );

        return result;
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
    @Override
    public boolean placePlayerShip(Boat boat, int row, int column, boolean horizontal) {
        if (currentMatch == null) {
            return false;
        }
        return currentMatch.getPlayer().getOwnBoard().placeShip(boat, row, column, horizontal);
    }

    /**
     * Resets the player's own board
     */
    @Override
    public void resetPlayerBoard() {
        if (currentMatch != null) {
            currentMatch.getPlayer().getOwnBoard().initialize();
        }
    }

    /**
     * The method checks if it's the player's turn
     */
    @Override
    public boolean isPlayerTurn() {
        return currentMatch != null && currentMatch.isPlayerTurn();
    }

    /**
     * Verify if the game has finished
     */
    @Override
    public boolean isGameFinished() {
        return currentMatch != null && currentMatch.isGameFinished();
    }

    /**
     * Obtains the winner of the game
     * @return
     */
    @Override
    public Player getWinner() {
        return currentMatch != null ? currentMatch.getWinner() : null;
    }

    /**
     * Obtains the current match
     * @return Current match
     */
    @Override
    public Match getCurrentMatch() {
        return currentMatch;
    }

    /**
     * Restart the game, resetting both players' boards
     */
    @Override
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
    @Override
    public int[][] getPlayerOwnBoardState() {
        if (currentMatch == null) return new int[10][10];
        return currentMatch.getPlayer().getOwnBoard().getBoardState();
    }

    /**
     * Obtains the state of the player's attack board (or enemy board)
     * @return Matrix with the state of the attack board (or enemy board)
     */
    @Override
    public int[][] getPlayerAttackBoardState() {
        if (currentMatch == null) return new int[10][10];
        return currentMatch.getPlayer().getAttackBoard().getBoardState();
    }
    /**
     * Helper method to validate game state
     * S - Single Responsibility: Validation logic separated
     */
    private boolean validateGameState() {
        return currentMatch != null &&
                currentMatch.isPlayerTurn() &&
                !currentMatch.isGameFinished();
    }
}
