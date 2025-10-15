package Model.Player.Interfaces;

import Model.Board.Board;

public interface IAttackCoordinatesValidator {
    void validateAttackCoordinates (int row, int column, Board board);
}
