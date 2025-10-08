package Services;

import Model.Boat.Boat;
import Model.Board.Board;
import java.util.Random;

/**
 * Ship Placement Service - Single Responsibility Principle
 * This class is ONLY responsible for ship placement logic
 * Separates placement concerns from game controller
 */
public class ShipPlacementService implements IShipPlacementService {

    private Random random;
    private static final int MAX_PLACEMENT_ATTEMPTS = 100;

    public ShipPlacementService() {
        this.random = new Random();
    }

    @Override
    public boolean placeShipRandomly(Board board, Boat boat) {
        int attempts = 0;

        while (attempts < MAX_PLACEMENT_ATTEMPTS) {
            int row = random.nextInt(board.getRows());
            int column = random.nextInt(board.getColumns());
            boolean horizontal = random.nextBoolean();

            if (board.placeShip(boat, row, column, horizontal)) {
                return true;
            }

            attempts++;
        }

        return false;
    }

    @Override
    public boolean placeFleetAutomatically(Board board, Boat[] fleet) {
        for (Boat boat : fleet) {
            if (!placeShipRandomly(board, boat)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isValidPlacement(Board board, Boat boat, int row, int column, boolean horizontal) {
        // Check bounds
        if (horizontal && column + boat.getLength() > board.getColumns()) {
            return false;
        }

        if (!horizontal && row + boat.getLength() > board.getRows()) {
            return false;
        }

        // Check overlapping
        int[][] boardState = board.getBoardState();
        for (int i = 0; i < boat.getLength(); i++) {
            int checkRow = horizontal ? row : row + i;
            int checkCol = horizontal ? column + i : column;

            if (boardState[checkRow][checkCol] == 1) {
                return false;
            }
        }

        return true;
    }
}
