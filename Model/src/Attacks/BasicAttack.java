package Attacks;

import Model.Board.Board;

public class BasicAttack extends Attack {
    public BasicAttack(int row, int column) {
        super(row, column);
    }

    @Override
    public String apply(Board targetBoard) {
        return targetBoard.shootEnemyBoat(row, column);
    }
}
