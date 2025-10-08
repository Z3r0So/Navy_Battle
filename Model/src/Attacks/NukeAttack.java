package Attacks;
import Model.Board.Board;

/**
 * Nuke Attack - Attacks a 3x3 area
 * The player chooses the center cell, and the attack hits:
 * - The center cell
 * - All 8 surrounding cells (if they exist within board bounds)
 */
public class NukeAttack extends Attack {

    public NukeAttack(int row, int column) {
        super(row, column);
    }
    /**
     * Applies the Nuke attack to the target board.
     * Attacks a 3x3 area centered on (row, column).
     * Counts hits and misses, and returns a summary string.
     *
     * @param targetBoard The board to attack.
     * @return A summary of the attack results.
    * */
    @Override
    public String apply(Board targetBoard) {
        int hits = 0;
        int misses = 0;
        StringBuilder result = new StringBuilder("Nuke attack on (" + row + "," + column + "): ");

        int[][] nukePattern = {
                {row - 1, column - 1}, //Top-left
                {row - 1, column},     //Top-center
                {row - 1, column + 1}, //Top-right
                {row, column - 1},     //Middle-left
                {row, column},         //Center
                {row, column + 1},     //Middle-right
                {row + 1, column - 1}, //Bottom-left
                {row + 1, column},     //Bottom-center
                {row + 1, column + 1}  //Bottom-right
        };

        // Execute attack on all cells in the pattern
        for (int[] pos : nukePattern) {
            int r = pos[0];
            int c = pos[1];

            // Verify if the coordinates are within the board limits
            if (r >= 0 && r < targetBoard.getRows() &&
                    c >= 0 && c < targetBoard.getColumns()) {

                String cellResult = targetBoard.shootEnemyBoat(r, c);

                if (cellResult.contains("Hit") || cellResult.contains("Sunk")) {
                    hits++;
                } else if (cellResult.contains("Miss")) {
                    misses++;
                }
            }
        }

        result.append(hits).append(" hits, ").append(misses).append(" misses.");
        return result.toString();
    }

    @Override
    public String toString() {
        return "NukeAttack at (" + row + "," + column + ")";
    }
}
