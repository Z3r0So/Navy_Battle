package Controller.Interfaces;

import Model.Boat.Boat;

public interface IBoardController {
    boolean placePlayerShip(Boat boat, int row, int column, boolean horizontal);
    void resetPlayerBoard();
    int[][] getPlayerOwnBoardState();
    int[][] getPlayerAttackBoardState();
}
