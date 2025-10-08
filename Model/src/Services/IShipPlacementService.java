package Services;
import Model.Board.Board;
import Model.Boat.Boat;

public interface IShipPlacementService {
        boolean placeShipRandomly(Board board, Boat boat);
        boolean placeFleetAutomatically(Board board, Boat[] fleet);
        boolean isValidPlacement(Board board, Boat boat, int row, int column, boolean horizontal);
    }

