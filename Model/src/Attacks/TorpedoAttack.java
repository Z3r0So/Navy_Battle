package Attacks;
import Model.Board.Board;
/**
 * Torpedo Attack - Attacks an entire row or column
 * The player chooses a cell, and the attack hits either:
 * - The entire row (horizontal torpedo)
 * - The entire column (vertical torpedo)
 */
public class TorpedoAttack extends Attack {
    private boolean isHorizontal; // true = row, false = column
    /**
     * Constructor for TorpedoAttack.
     *
     * @param row The row index of the chosen cell.
     * @param column The column index of the chosen cell.
     * @param isHorizontal True for horizontal attack (entire row), false for vertical attack (entire column).
    * */
    public TorpedoAttack(int row, int column, boolean isHorizontal) {
        super(row, column);
        this.isHorizontal = isHorizontal;
    }
    /**
     * Applies the Torpedo attack to the target board.
     * Attacks an entire row or column based on isHorizontal flag.
     * Counts hits and misses, and returns a summary string.
     *
     * @param targetBoard The board to attack.
     * @return A summary of the attack results.
    * */
    @Override
    public String apply(Board targetBoard) {
        int hits = 0;
        int misses = 0;
        StringBuilder result = new StringBuilder("Torpedo attack ");

        if (isHorizontal) {
            result.append("on row ").append(row).append(": ");

            for (int c = 0; c < targetBoard.getColumns(); c++) {
                String cellResult = targetBoard.shootEnemyBoat(row, c);

                if (cellResult.contains("Hit") || cellResult.contains("Sunk")) {
                    hits++;
                } else if (cellResult.contains("Miss")) {
                    misses++;
                }
            }
        } else {
            result.append("on column ").append(column).append(": ");

            // Attack entire column
            for (int r = 0; r < targetBoard.getRows(); r++) {
                String cellResult = targetBoard.shootEnemyBoat(r, column);

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
    /**
     * Returns a string representation of the Torpedo attack.
     *
     * @return A string describing the attack type and coordinates.
    * */
    @Override
    public String toString() {
        return "TorpedoAttack " + (isHorizontal ? "horizontal" : "vertical") +
                " at (" + row + "," + column + ")";
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }
}
