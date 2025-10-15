package Model.Player;

import Model.Board.Board;
import Model.Player.Interfaces.IMachineStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StandardMachineStrategy implements IMachineStrategy {
        private final Random random;

        public StandardMachineStrategy() {
            this.random = new Random();
        }
        /**
         * Chooses attack coordinates based on the current mode (hunt or target)
         * Hunt mode uses a checkerboard pattern for efficiency
         * Target mode focuses on adjacent cells and linear continuations
         * @param enemyBoard is the opponent's board
         * @param isTargetMode indicates if we are in target mode
         * @param targetStack is the stack of target coordinates to consider
         * @param hitHistory is the history of successful hits
         * @return an array with the chosen attack coordinates [row, column]
         */
        @Override
        public int[] chooseAttackCoordinates(
                Board enemyBoard,
                boolean isTargetMode,
                List<int[]> targetStack,
                List<int[]> hitHistory
        ) {
            if (isTargetMode && !targetStack.isEmpty()) {
                return targetModeAttack(enemyBoard, targetStack, hitHistory);
            } else {
                return huntModeAttack(enemyBoard);
            }
        }

        /**
         * Hunt mode is based on the use of a checkerboard pattern for efficient coverage
         * This pattern ensures we hit every ship of length 2 or more
         */
        private int[] huntModeAttack(Board enemyBoard) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if ((i + j) % 2 == 0 && enemyBoard.validShoot(i, j)) {
                        return new int[]{i, j};
                    }
                }
            }
            return getAnyValidPosition(enemyBoard);
        }

        /**
         * Target mode is based on the focus of adjacent cells and linear continuations
         * If we have 2+ hits, we try to continue linearly first
         * Otherwise, we pop from the target stack until we find a valid shot
         * If the stack is exhausted, we fallback to hunt mode
         */
        private int[] targetModeAttack(
                Board enemyBoard,
                List<int[]> targetStack,
                List<int[]> hitHistory
        ) {
            if (hitHistory.size() >= 2) {
                int[] linearTarget = getLinearContinuation(enemyBoard, hitHistory);
                if (linearTarget != null) {
                    removeFromList(targetStack, linearTarget);
                    return linearTarget;
                }
            }

            while (!targetStack.isEmpty()) {
                int[] target = targetStack.remove(0);
                if (enemyBoard.validShoot(target[0], target[1])) {
                    return target;
                }
            }

            return huntModeAttack(enemyBoard);
        }

        /**
         * Finds linear continuation of aligned hits
         * If last 2 hits are aligned, continues in that direction
         */
        private int[] getLinearContinuation(Board enemyBoard, List<int[]> hitHistory) {
            if (hitHistory.size() < 2) return null;

            int[] lastHit = hitHistory.get(hitHistory.size() - 1);
            int[] secondLastHit = hitHistory.get(hitHistory.size() - 2);

            int deltaRow = lastHit[0] - secondLastHit[0];
            int deltaCol = lastHit[1] - secondLastHit[1];

            //Check if hits are aligned (not diagonal, not same cell)
            if ((deltaRow != 0 && deltaCol != 0) || (deltaRow == 0 && deltaCol == 0)) {
                return null;
            }

            //After the verifying, it tries to continue in the same direction
            int nextRow = lastHit[0] + deltaRow;
            int nextCol = lastHit[1] + deltaCol;

            if (isValidCoordinate(nextRow, nextCol) && enemyBoard.validShoot(nextRow, nextCol)) {
                return new int[]{nextRow, nextCol};
            }

            //Tries opposite direction
            int prevRow = secondLastHit[0] - deltaRow;
            int prevCol = secondLastHit[1] - deltaCol;

            if (isValidCoordinate(prevRow, prevCol) && enemyBoard.validShoot(prevRow, prevCol)) {
                return new int[]{prevRow, prevCol};
            }

            return null;
        }

        /**
         * Gets any valid attack position if there is no better option
         */
        private int[] getAnyValidPosition(Board enemyBoard) {
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
        /**
         * Checks if the given coordinates are within the board bounds
         * @param row is the row to check
         * @param col is the column to check
         * @return true if the coordinates are valid, false otherwise
         */
        private boolean isValidCoordinate(int row, int col) {
            return row >= 0 && row < 10 && col >= 0 && col < 10;
        }
        /** Removes a specific coordinate from a list of coordinates
         * @param list is the list from which we want to remove the coordinate
         * @param target is the coordinate we want to remove
         * */
        private void removeFromList(List<int[]> list, int[] target) {
            list.removeIf(item -> item[0] == target[0] && item[1] == target[1]);
        }
}
