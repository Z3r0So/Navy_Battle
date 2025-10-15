package Model.Player;

import Attacks.Attack;
import Attacks.BasicAttack;
import Model.Board.Board;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Machine extends Player {
    private Random random;

    private boolean targetMode; // false = Hunt, true = Target
    private List<int[]> targetStack;
    private List<int[]> hitHistory;

    public Machine() {
        super("Machine", "none");
        this.random = new Random();
        this.targetMode = false;
        this.targetStack = new ArrayList<>();
        this.hitHistory = new ArrayList<>();
    }
    /**
     * Method to make an attack on the enemy board.
     * The machine uses two strategies: Hunt Mode and Target Mode.
     * In Hunt Mode, it attacks in a checkerboard pattern to maximize coverage.
     * In Target Mode, it focuses on adjacent cells after a hit to try to sink the ship.
     * The machine switches to Target Mode after a hit and reverts to Hunt Mode after
     * sinking a ship or exhausting all target options.
     * @param enemyBoard The board of the enemy player where the attack will be made.
     * @return An Attack object representing the chosen attack coordinates.
    * */
    @Override
    public Attack makeAttack(Board enemyBoard) {
        int[] coordinates;

        if (targetMode && !targetStack.isEmpty()) {
            coordinates = targetModeAttack(enemyBoard);
        } else {
            coordinates = huntModeAttack(enemyBoard);
            targetMode = false;
        }

        return new BasicAttack(coordinates[0], coordinates[1]);
    }

    /**Method for hunt mode attack strategy.
     * The machine attacks in a checkerboard pattern to maximize the chance of hitting ships.
     * It first tries to hit all "white" squares (where (row + column) is even).
     * If all such squares are exhausted, it will attack any remaining valid positions.
     *
     * @param enemyBoard The board of the enemy player where the attack will be made.
     * @return An array with the chosen attack coordinates [row, column].
    * */
     public int[] huntModeAttack(Board enemyBoard) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if ((i + j) % 2 == 0 && enemyBoard.validShoot(i, j)) {
                    return new int[]{i, j};
                }
            }
        }
        return getAnyValidPosition(enemyBoard);
    }

    /**Method for target mode attack strategy.
     * The machine focuses on attacking adjacent cells of previously hit ships.
     * It prioritizes continuing in a straight line if multiple hits are aligned.
     * If no linear continuation is possible, it attacks the next valid target from the stack.
     * If all targets are exhausted, it reverts back to hunt mode.
     *
     * @param enemyBoard The board of the enemy player where the attack will be made.
     * @return An array with the chosen attack coordinates [row, column].
    * */
    public int[] targetModeAttack(Board enemyBoard) {
        if (hitHistory.size() >= 2) {
            int[] optimizedTarget = getLinearContinuation(enemyBoard);
            if (optimizedTarget != null) {
                removeFromTargetStack(optimizedTarget);
                return optimizedTarget;
            }
        }

        while (!targetStack.isEmpty()) {
            int[] target = targetStack.remove(0);
            if (enemyBoard.validShoot(target[0], target[1])) {
                return target;
            }
        }

        targetMode = false;
        return huntModeAttack(enemyBoard);
    }

    /**Method to find a linear continuation of hits.
     * If the last two hits are aligned (horizontally or vertically), it attempts to continue
     * attacking in that direction. If the next position in that direction is invalid or already
     * attacked, it tries the opposite direction. If neither direction is valid, it returns null.
     *
     * @param enemyBoard The board of the enemy player where the attack will be made.
     * @return An array with the next attack coordinates [row, column] if a continuation is possible,
     *         otherwise null.
    * */
    public  int[] getLinearContinuation(Board enemyBoard) {
        if (hitHistory.size() < 2) return null;

        int[] lastHit = hitHistory.get(hitHistory.size() - 1);
        int[] secondLastHit = hitHistory.get(hitHistory.size() - 2);

        int deltaRow = lastHit[0] - secondLastHit[0];
        int deltaCol = lastHit[1] - secondLastHit[1];

        if (deltaRow != 0 && deltaCol != 0) return null;
        if (deltaRow == 0 && deltaCol == 0) return null;

        int nextRow = lastHit[0] + deltaRow;
        int nextCol = lastHit[1] + deltaCol;

        if (isValidCoordinate(nextRow, nextCol) &&
                enemyBoard.validShoot(nextRow, nextCol)) {
            return new int[]{nextRow, nextCol};
        }

        int prevRow = secondLastHit[0] - deltaRow;
        int prevCol = secondLastHit[1] - deltaCol;

        if (isValidCoordinate(prevRow, prevCol) &&
                enemyBoard.validShoot(prevRow, prevCol)) {
            return new int[]{prevRow, prevCol};
        }

        return null;
    }
    /**
     * Method to notify the machine of the result of its last attack.
     * This method updates the machine's internal state based on whether the last attack
     * was a hit, miss, or sunk a ship. It manages the transition between hunt mode and
     * target mode, and updates the target stack with adjacent positions after a hit.
    * */
    public void notifyAttackResult(int row, int column, String result, Board enemyBoard) {
        if (result.contains("Hit") || result.contains("Sunk")) {
            hitHistory.add(new int[]{row, column});

            if (result.contains("Sunk")) {
                targetMode = false;
                targetStack.clear();
            } else {
                targetMode = true;
                addAdjacentTargets(row, column, enemyBoard);
            }
        }
    }

    /**Method to add adjacent targets to the target stack.
     * After a successful hit, this method adds the valid adjacent cells (up, down, left, right)
     * to the target stack for future attacks. It ensures that only valid and untried positions
     * are added, avoiding duplicates.
     * @param row The row of the last successful hit.
     * @param column The column of the last successful hit.
     * @param enemyBoard The board of the enemy player where the attack will be made.
    * */
    public  void addAdjacentTargets(int row, int column, Board enemyBoard) {
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}}; //

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = column + dir[1];

            if (isValidCoordinate(newRow, newCol) &&
                    enemyBoard.validShoot(newRow, newCol)) {

                if (!containsTarget(newRow, newCol)) {
                    targetStack.add(new int[]{newRow, newCol});
                }
            }
        }
    }
    /**Method to get any valid attack position.
     * If no preferred strategy positions are available, this method scans the entire board
     * for any valid attack position and randomly selects one. This ensures that the machine
     * can always make a move even when specific strategies are exhausted.
     *
     * @param enemyBoard The board of the enemy player where the attack will be made.
     * @return An array with the chosen attack coordinates [row, column].
     * @throws IllegalStateException if no valid attack positions are available.
    * */
    public  int[] getAnyValidPosition(Board enemyBoard) {
        List<int[]> validPositions = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (enemyBoard.validShoot(i, j)) {
                    validPositions.add(new int[]{i, j});
                }
            }
        }

        if (validPositions.isEmpty()) {
            throw new IllegalStateException("No valid attacks available!");
        }

        return validPositions.get(random.nextInt(validPositions.size()));
    }
    /**Method to check if coordinates are within board bounds.
     *
     * @param row The row coordinate to check.
     * @param col The column coordinate to check.
     * @return true if the coordinates are valid (within 0-9), false otherwise.
    * */
    public  boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 10;
    }
    /**Method to check if a target is already in the target stack.
     *
     * @param row The row coordinate of the target to check.
     * @param col The column coordinate of the target to check.
     * @return true if the target is already in the stack, false otherwise.
    * */
    public  boolean containsTarget(int row, int col) {
        return targetStack.stream().anyMatch(target ->
                target[0] == row && target[1] == col);
    }
    /**Method to remove a specific target from the target stack.
     * This is used to clean up the target stack when a target has been successfully attacked
     * or when optimizing attack strategies.
     *
     * @param targetToRemove An array with the coordinates of the target to remove [row, column].
     */
    public void removeFromTargetStack(int[] targetToRemove) {
        targetStack.removeIf(target ->
                target[0] == targetToRemove[0] && target[1] == targetToRemove[1]);
    }
    // Getters for testing and debugging
    public boolean isInTargetMode() { return targetMode; }
    public int getTargetStackSize() { return targetStack.size(); }
    public int getHitHistorySize() { return hitHistory.size(); }

    @Override
    public String toString() {
        return "Machine Player (" +
                (targetMode ? "Target Mode" : "Hunt Mode") +
                ", Targets: " + targetStack.size() + ")";
    }
}