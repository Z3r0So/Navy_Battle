package Services;

import Model.Boat.Boat;
import Model.Board.Board;
import Services.Interfaces.IShipPlacementService;

import java.util.Random;

/**
 * Ship Placement Service - Single Responsibility Principle
 * This class is ONLY responsible for ship placement logic
 * Separates placement concerns from game controller
 */
public class ShipPlacementService implements IShipPlacementService {

    private Random random;
    private static final int MAX_PLACEMENT_ATTEMPTS = 100;
    private static final int MAX_FLEET_ATTEMPTS = 10;
    /**
     * Constructor for creating random bound value
    * */
    public ShipPlacementService() {
        this.random = new Random();
    }
    /** Method to place a ship randomly on the board
     * @param board is the board where we want to place the ship
     * @param boat is the boat that we want to place
     * @return true if the ship was placed successfully, false otherwise
    * */
    @Override
    public boolean placeShipRandomly(Board board, Boat boat) {
        int attempts = 0;

        while (attempts < MAX_PLACEMENT_ATTEMPTS) {
            int row = random.nextInt(board.getRows()); //With .nextInt we get a random number between 0 and the value passed as parameter (10 in this case but exclusive so 0-9)
            int column = random.nextInt(board.getColumns());
            boolean horizontal = random.nextBoolean();
            if (isValidPlacement(board, boat, row, column, horizontal)) {
                boolean placed = board.placeShip(boat, row, column, horizontal);
                if (placed) {
                    return true;
                }
            }
            attempts++;
        }

        return false;
    }
    /** Method to place a fleet of ships automatically on the board
    * @param board is the board where we want to place the fleet
    * @param fleet is the array of boats that we want to place
    * @return true if all ships were placed successfully, false otherwise
    * */
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
            int checkRow;
            int checkCol;

            if (horizontal) {
                checkRow = row;
                checkCol = column + i;
            } else {
                checkRow = row + i;
                checkCol = column;
            }

            if (boardState[checkRow][checkCol] == 1) {
                return false;
            }
        }

        return true;
    }
}
