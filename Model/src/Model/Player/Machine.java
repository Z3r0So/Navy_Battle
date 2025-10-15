package Model.Player;

import Attacks.Attack;
import Attacks.BasicAttack;
import Model.Board.Board;
import Model.Player.Interfaces.IMachineStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Machine extends Player {
    private final IMachineStrategy attackStrategy;

    // AI State - managed by Machine, used by Strategy
    private boolean targetMode;
    private List<int[]> targetStack;
    private List<int[]> hitHistory;

    /**
     * Constructor with dependency injection
     *
     * @param attackStrategy The strategy to use for choosing attack coordinates
     */
    public Machine(IMachineStrategy attackStrategy) {
        super("Machine", "none");
        this.attackStrategy = attackStrategy;
        this.targetMode = false;
        this.targetStack = new ArrayList<>();
        this.hitHistory = new ArrayList<>();
    }

    /**
     * Default constructor using StandardMachineStrategy
     * For backward compatibility
     */
    public Machine() {
        this(new StandardMachineStrategy());
    }

    /**
     * Makes an attack using the injected strategy
     * Delegates coordinate selection to the strategy
     *
     * @param enemyBoard The board to attack
     * @return Attack object with chosen coordinates
     */
    @Override
    public Attack makeAttack(Board enemyBoard) {
        // Delegate to strategy (SRP + DIP)
        int[] coordinates = attackStrategy.chooseAttackCoordinates(
                enemyBoard,
                targetMode,
                targetStack,
                hitHistory
        );

        return new BasicAttack(coordinates[0], coordinates[1]);
    }

    /**
     * Notifies the machine of an attack result
     * Updates internal state for future attacks
     * This method manages the transition between hunt and target modes
     *
     * @param row        Row that was attacked
     * @param column     Column that was attacked
     * @param result     Result of the attack ("Hit", "Miss", "Sunk")
     * @param enemyBoard The enemy board (for validation)
     */
    public void notifyAttackResult(int row, int column, String result, Board enemyBoard) {
        if (result.contains("Hit") || result.contains("Sunk")) {
            // Record the hit
            hitHistory.add(new int[]{row, column});

            if (result.contains("Sunk")) {
                // Ship sunk - return to hunt mode
                targetMode = false;
                targetStack.clear();
            } else {
                // Hit but not sunk - enter/stay in target mode
                targetMode = true;
                addAdjacentTargets(row, column, enemyBoard);
            }
        }
    }

    /**
     * Adds adjacent cells to the target stack after a hit
     * Only adds valid, unattacked positions
     *
     * @param row        Row of the hit
     * @param column     Column of the hit
     * @param enemyBoard The enemy board for validation
     */
    private void addAdjacentTargets(int row, int column, Board enemyBoard) {
        // Cardinal directions: up, down, left, right
        int[][] directions = {
                {-1, 0},  // Up
                {1, 0},   // Down
                {0, -1},  // Left
                {0, 1}    // Right
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = column + dir[1];

            // Check if position is valid and not already targeted
            if (isValidCoordinate(newRow, newCol) &&
                    enemyBoard.validShoot(newRow, newCol) &&
                    !containsTarget(newRow, newCol)) {

                targetStack.add(new int[]{newRow, newCol});
            }
        }
    }

    /**
     * Checks if coordinates are within board bounds
     *
     * @param row Row to check
     * @param col Column to check
     * @return true if valid, false otherwise
     */
    private boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 10;
    }

    /**
     * Checks if a target is already in the target stack
     *
     * @param row Row to check
     * @param col Column to check
     * @return true if target exists in stack, false otherwise
     */
    private boolean containsTarget(int row, int col) {
        return targetStack.stream()
                .anyMatch(target -> target[0] == row && target[1] == col);
    }

    /**
     * Resets the machine's AI state
     * Useful for starting a new game
     */
    public void resetAIState() {
        targetMode = false;
        targetStack.clear();
        hitHistory.clear();
    }

    // Getters for testing and debugging
    public boolean isInTargetMode() {
        return targetMode;
    }

    public int getTargetStackSize() {
        return targetStack.size();
    }

    public int getHitHistorySize() {
        return hitHistory.size();
    }

    public List<int[]> getTargetStack() {
        return new ArrayList<>(targetStack);
    }

    public List<int[]> getHitHistory() {
        return new ArrayList<>(hitHistory);
    }

    @Override
    public String toString() {
        return "Machine Player (" +
                (targetMode ? "Target Mode" : "Hunt Mode") +
                ", Targets: " + targetStack.size() +
                ", Hits: " + hitHistory.size() + ")";
    }
}