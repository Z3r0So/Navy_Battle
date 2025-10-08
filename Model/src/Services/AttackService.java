package Services;

import Attacks.Attack;
import Model.Board.Board;

/**
 * Attack Service
 * This class is ONLY responsible for attack execution and marking logic
 * Separates attack concerns from game controller
 */
public class AttackService implements IAttackService {

    @Override
    public String executeAttack(Attack attack, Board targetBoard) {
        return attack.apply(targetBoard);
    }

    @Override
    public void markAttackOnBoard(Board attackBoard, int row, int column, boolean wasHit) {
        if (row >= 0 && row < attackBoard.getRows() &&
                column >= 0 && column < attackBoard.getColumns()) {
            attackBoard.markAttack(row, column, wasHit);
        }
    }

    @Override
    public void markCrossAttackPattern(Board attackBoard, Board enemyBoard, int centerRow, int centerCol) {
        int[][] crossPattern = {
                {centerRow, centerCol},           // Center
                {centerRow - 1, centerCol},       // Up
                {centerRow + 1, centerCol},       // Down
                {centerRow, centerCol - 1},       // Left
                {centerRow, centerCol + 1}        // Right
        };

        for (int[] pos : crossPattern) {
            int r = pos[0];
            int c = pos[1];

            if (r >= 0 && r < attackBoard.getRows() &&
                    c >= 0 && c < attackBoard.getColumns()) {

                int[][] enemyBoardState = enemyBoard.getBoardState();
                boolean wasHit = (enemyBoardState[r][c] == 3); // 3 = hit

                markAttackOnBoard(attackBoard, r, c, wasHit);
            }
        }
    }

    @Override
    public void markTorpedoAttackPattern(Board attackBoard, Board enemyBoard, int row, int column, boolean isHorizontal) {
        if (isHorizontal) {
            // Mark entire row
            for (int c = 0; c < attackBoard.getColumns(); c++) {
                int[][] enemyBoardState = enemyBoard.getBoardState();
                boolean wasHit = (enemyBoardState[row][c] == 3);
                markAttackOnBoard(attackBoard, row, c, wasHit);
            }
        } else {
            // Mark entire column
            for (int r = 0; r < attackBoard.getRows(); r++) {
                int[][] enemyBoardState = enemyBoard.getBoardState();
                boolean wasHit = (enemyBoardState[r][column] == 3);
                markAttackOnBoard(attackBoard, r, column, wasHit);
            }
        }
    }

    @Override
    public void markNukeAttackPattern(Board attackBoard, Board enemyBoard, int centerRow, int centerCol) {
        int[][] nukePattern = {
                {centerRow - 1, centerCol - 1}, // Top-left
                {centerRow - 1, centerCol},     // Top-center
                {centerRow - 1, centerCol + 1}, // Top-right
                {centerRow, centerCol - 1},     // Middle-left
                {centerRow, centerCol},         // Center
                {centerRow, centerCol + 1},     // Middle-right
                {centerRow + 1, centerCol - 1}, // Bottom-left
                {centerRow + 1, centerCol},     // Bottom-center
                {centerRow + 1, centerCol + 1}  // Bottom-right
        };

        for (int[] pos : nukePattern) {
            int r = pos[0];
            int c = pos[1];

            if (r >= 0 && r < attackBoard.getRows() &&
                    c >= 0 && c < attackBoard.getColumns()) {

                int[][] enemyBoardState = enemyBoard.getBoardState();
                boolean wasHit = (enemyBoardState[r][c] == 3);

                markAttackOnBoard(attackBoard, r, c, wasHit);
            }
        }
    }
}