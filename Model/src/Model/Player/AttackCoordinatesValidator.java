package Model.Player;

import Model.Board.Board;
import Model.Player.Interfaces.IAttackCoordinatesValidator;

public class AttackCoordinatesValidator implements IAttackCoordinatesValidator {
    @Override
    public void validateAttackCoordinates(int row, int column, Board board) {
        //The first verification is to ensure that the coordinates have been set.
        if (row == -1 || column == -1) {
            throw new IllegalStateException(
                    "No attack coordinates set! Call setNextAttack() first."
            );
        }

        //Check if the coordinates are within the board bounds
        if (!isWithinBounds(row, column, board)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Attack coordinates (%d,%d) out of bounds! Valid range: 0-%d, 0-%d",
                            row, column,
                            board.getRows() - 1,
                            board.getColumns() - 1
                    )
            );
        }

        //Check if the position has already been attacked
        if (!board.validShoot(row, column)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Position (%d,%d) has already been attacked!",
                            row, column
                    )
            );
        }
    }

    /**
     * Helper method to check if coordinates are within board bounds
     *
     * @param row The row coordinate
     * @param column The column coordinate
     * @param board The board to check against
     * @return true if coordinates are valid, false otherwise
     */
    private boolean isWithinBounds(int row, int column, Board board) {
        return row >= 0 && row < board.getRows()
                && column >= 0 && column < board.getColumns();
    }
}
