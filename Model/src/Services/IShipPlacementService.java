package Services;
import Model.Board.Board;
import Model.Boat.Boat;

public interface IShipPlacementService {
    /**
     * Place a single ship randomly on the board
     */
        boolean placeShipRandomly(Board board, Boat boat);
    /**
     * Place an entire fleet of ships automatically
     */
        boolean placeFleetAutomatically(Board board, Boat[] fleet);
        boolean isValidPlacement(Board board, Boat boat, int row, int column, boolean horizontal);
    }

