package Controller;

import Attacks.Attack;
import Attacks.CrossBombAttack;
import Attacks.NukeAttack;
import Attacks.TorpedoAttack;
import Controller.Interfaces.*;
import Model.Boat.*;
import Model.Match.Match;
import Model.Player.HumanPlayer;
import Model.Player.Interfaces.IPlayerCreator;
import Services.*;
import Model.Player.Player;
import Services.Interfaces.IAttackExecutor;
import Services.Interfaces.IAttackService;

public class GameController implements
        IGameLifecycle,
        IAttackController,
        IBoardController,
        ITurnController {

    private Match currentMatch;

    // Injected dependencies (DIP)
    private final IPlayerCreator playerCreator;
    private final IFleetManager fleetManager;
    private final IAttackExecutor attackExecutor;
    private final IAttackService attackService;

    /**
     * Constructor with full dependency injection
     * Allows complete control over dependencies for testing
     *
     * @param playerCreator Factory for creating players
     * @param fleetManager Manager for fleet operations
     * @param attackExecutor Executor for attack operations
     * @param attackService Service for marking attacks
     */
    public GameController(
            IPlayerCreator playerCreator,
            IFleetManager fleetManager,
            IAttackExecutor attackExecutor,
            IAttackService attackService
    ) {
        this.playerCreator = playerCreator;
        this.fleetManager = fleetManager;
        this.attackExecutor = attackExecutor;
        this.attackService = attackService;
    }

    // ==================== IGameLifecycle Implementation ====================

    /**
     * Starts a new game between human and machine
     * Delegates creation and setup to injected dependencies
     *
     * @param playerName Name of the human player
     * @param password Password of the human player
     * @return true if game started successfully, false otherwise
     */
    @Override
    public boolean startNewGame(String playerName, String password) {
        try {
            // Use player creator (DIP)
            Player humanPlayer = playerCreator.createHumanPlayer(playerName, password);
            Player machinePlayer = playerCreator.createMachinePlayer();

            // Create match
            currentMatch = new Match(humanPlayer, machinePlayer);

            // Create and deploy fleets using fleet manager (SRP)
            Boat[] playerFleet = fleetManager.createStandardFleet();
            Boat[] machineFleet = fleetManager.createStandardFleet();

            boolean playerShipsPlaced = fleetManager.deployFleet(
                    humanPlayer.getOwnBoard(),
                    playerFleet
            );

            boolean machineShipsPlaced = fleetManager.deployFleet(
                    machinePlayer.getOwnBoard(),
                    machineFleet
            );

            // Validate deployment
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

    @Override
    public void resetGame() {
        if (currentMatch != null) {
            currentMatch.getPlayer().resetBoards();
            currentMatch.getMachine().resetBoards();
        }
        currentMatch = null;
    }

    @Override
    public boolean isGameFinished() {
        return currentMatch != null && currentMatch.isGameFinished();
    }

    @Override
    public Player getWinner() {
        return currentMatch != null ? currentMatch.getWinner() : null;
    }

    @Override
    public Match getCurrentMatch() {
        return currentMatch;
    }

    // ==================== IAttackController Implementation ====================

    /**
     * Executes a basic player attack
     * Delegates to attack executor (SRP)
     *
     * @param row Row to attack
     * @param column Column to attack
     * @return Result message of the attack
     */
    @Override
    public String playerAttack(int row, int column) {
        if (!validateGameState()) {
            return "No game in progress!";
        }

        try {
            HumanPlayer human = (HumanPlayer) currentMatch.getPlayer();
            human.setNextAttack(row, column);

            Attack attack = human.makeAttack(currentMatch.getMachine().getOwnBoard());

            // Use attack executor (SRP + DIP)
            AttackResult result = attackExecutor.executeAttack(
                    currentMatch,
                    attack,
                    currentMatch.getPlayer(),
                    currentMatch.getMachine()
            );

            return result.getMessage();

        } catch (IllegalArgumentException | IllegalStateException e) {
            return "Invalid attack: " + e.getMessage();
        }
    }

    /**
     * Executes a cross bomb attack
     *
     * @param row Center row of the cross
     * @param column Center column of the cross
     * @return Result message of the attack
     */
    @Override
    public String playerCrossBombAttack(int row, int column) {
        if (!validateGameState()) {
            return "Invalid game state!";
        }

        HumanPlayer human = (HumanPlayer) currentMatch.getPlayer();

        if (!human.getPowerUps().hasCrossBombs()) {
            return "No cross bombs available!";
        }

        // Use power-up
        human.getPowerUps().useCrossBomb();

        // Create and execute attack
        Attack attack = new CrossBombAttack(row, column);
        AttackResult result = attackExecutor.executeAttack(
                currentMatch,
                attack,
                human,
                currentMatch.getMachine()
        );

        // Mark pattern on attack board (delegates to service)
        attackService.markCrossAttackPattern(
                human.getAttackBoard(),
                currentMatch.getMachine().getOwnBoard(),
                row,
                column
        );

        return result.getMessage();
    }

    /**
     * Executes a torpedo attack (entire row or column)
     *
     * @param row Row coordinate
     * @param column Column coordinate
     * @param isHorizontal true for row attack, false for column attack
     * @return Result message of the attack
     */
    @Override
    public String playerTorpedoAttack(int row, int column, boolean isHorizontal) {
        if (!validateGameState()) {
            return "Invalid game state!";
        }

        HumanPlayer human = (HumanPlayer) currentMatch.getPlayer();

        if (!human.getPowerUps().hasTorpedoes()) {
            return "No torpedoes available!";
        }

        // Use power-up
        human.getPowerUps().useTorpedo();

        // Create and execute attack
        Attack attack = new TorpedoAttack(row, column, isHorizontal);
        AttackResult result = attackExecutor.executeAttack(
                currentMatch,
                attack,
                human,
                currentMatch.getMachine()
        );

        // Mark pattern on attack board
        attackService.markTorpedoAttackPattern(
                human.getAttackBoard(),
                currentMatch.getMachine().getOwnBoard(),
                row,
                column,
                isHorizontal
        );

        return result.getMessage();
    }

    /**
     * Executes a nuke attack (3x3 area)
     *
     * @param row Center row of the nuke
     * @param column Center column of the nuke
     * @return Result message of the attack
     */
    @Override
    public String playerNukeAttack(int row, int column) {
        if (!validateGameState()) {
            return "Invalid game state!";
        }

        HumanPlayer human = (HumanPlayer) currentMatch.getPlayer();

        if (!human.getPowerUps().hasNukes()) {
            return "No nukes available!";
        }

        // Use power-up
        human.getPowerUps().useNuke();

        // Create and execute attack
        Attack attack = new NukeAttack(row, column);
        AttackResult result = attackExecutor.executeAttack(
                currentMatch,
                attack,
                human,
                currentMatch.getMachine()
        );

        // Mark pattern on attack board
        attackService.markNukeAttackPattern(
                human.getAttackBoard(),
                currentMatch.getMachine().getOwnBoard(),
                row,
                column
        );

        return result.getMessage();
    }

    /**
     * Executes the machine's attack
     * Delegates to attack executor (SRP)
     *
     * @return Result message of the attack
     */
    @Override
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
            // Use attack executor for machine attack (SRP + DIP)
            AttackResult result = attackExecutor.executeMachineAttack(currentMatch);
            return result.getMessage();

        } catch (Exception e) {
            return "Machine attack error: " + e.getMessage();
        }
    }

    // ==================== IBoardController Implementation ====================

    /**
     * Places a ship for the player at the specified position
     *
     * @param boat Boat to place
     * @param row Starting row
     * @param column Starting column
     * @param horizontal True if horizontal, false if vertical
     * @return true if placed successfully, false otherwise
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
     * Gets the state of the player's own board
     *
     * @return 2D array representing board state
     */
    @Override
    public int[][] getPlayerOwnBoardState() {
        if (currentMatch == null) {
            return new int[10][10];
        }
        return currentMatch.getPlayer().getOwnBoard().getBoardState();
    }

    /**
     * Gets the state of the player's attack board (tracking enemy hits)
     *
     * @return 2D array representing attack board state
     */
    @Override
    public int[][] getPlayerAttackBoardState() {
        if (currentMatch == null) {
            return new int[10][10];
        }
        return currentMatch.getPlayer().getAttackBoard().getBoardState();
    }

    // ==================== ITurnController Implementation ====================

    /**
     * Checks if it's the player's turn
     *
     * @return true if player's turn, false otherwise
     */
    @Override
    public boolean isPlayerTurn() {
        return currentMatch != null && currentMatch.isPlayerTurn();
    }

    // ==================== Private Helper Methods ====================

    /**
     * Validates that game is in a valid state for player actions
     * Extracted to separate method (SRP)
     *
     * @return true if game is valid for actions, false otherwise
     */
    private boolean validateGameState() {
        return currentMatch != null
                && currentMatch.isPlayerTurn()
                && !currentMatch.isGameFinished();
    }
        }
