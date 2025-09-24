package Model.Player;

import Attacks.Attack;
import Attacks.BasicAttack;
import Model.Board.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Machine extends Player {
    private Random random;
    private List<int[]> huntTargets;
    private List<int[]> hitHistory;
    public Machine() {
        super("Machine", "none");
        this.random = new Random();
        initializeAI();
    }

    private void initializeAI() {
        this.huntTargets = new ArrayList<>();
        this.hitHistory = new ArrayList<>();
    }
    public Attack selectRandomAttack() {
        int rows = attackBoard.getRows();
        int cols = attackBoard.getColumns();

        int maxAttempts = rows * cols;
        for (int i = 0; i < maxAttempts; i++) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if (attackBoard.validShoot(r, c)) {
                return new BasicAttack(r, c);
            }
        }
        return null;
    }

    @Override
    public Attack makeattack(Board enemyBoard) {
        return null;
    }
    /*
    * */
    private boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 10;
    }
    /*This method implements a checkerboard attack strategy, which is more efficient than random attacks.
    It attempts to hit cells in a checkerboard pattern, ensuring that ships of length 2 or greater are more likely to be found.
    If no valid checkerboard cell is available, it falls back to any valid cell.
    @return Attack object representing the selected attack coordinates
    * */
    public Attack selectCheckerboardAttack() {
        int rows = attackBoard.getRows();
        int cols = attackBoard.getColumns();

        // We try a number of random attempts to find a valid pair checkerboard cell
        int maxAttempts = rows * cols;
        for (int i = 0; i < maxAttempts; i++) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            if ((r + c) % 2 == 0 && attackBoard.validShoot(r, c)) {
                return new BasicAttack(r, c);
            }
        }

        // if no valid checkerboard cell found, fall back to any valid cell
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (attackBoard.validShoot(r, c)) {
                    return new BasicAttack(r, c);
                }
            }
        }

        return null;
    }
    public int[] getBestHuntTarget(Board enemyBoard) {
        // Priorizar targets que forman líneas con hits anteriores
        if (hitHistory.size() > 1) {
            int[] lastHit = hitHistory.get(hitHistory.size() - 1);
            int[] secondLastHit = hitHistory.get(hitHistory.size() - 2);

            // Si los dos últimos hits están en línea, continuar esa dirección
            int rowDiff = lastHit[0] - secondLastHit[0];
            int colDiff = lastHit[1] - secondLastHit[1];

            int nextRow = lastHit[0] + rowDiff;
            int nextCol = lastHit[1] + colDiff;

            if (isValidCoordinate(nextRow, nextCol) &&
                    enemyBoard.validShoot(nextRow, nextCol)) {
                // Remover de huntTargets si está ahí
                huntTargets.removeIf(target -> target[0] == nextRow && target[1] == nextCol);
                return new int[]{nextRow, nextCol};
            }
        }

        // Si no hay patrón claro, tomar el primer target disponible
        return huntTargets.remove(0);
    }
}
