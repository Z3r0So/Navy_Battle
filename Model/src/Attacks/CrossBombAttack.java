package Attacks;

import Model.Board.Board;

public class CrossBombAttack extends Attack{
    public CrossBombAttack(int row, int column) {
        super(row, column);
    }
    @Override
    public String apply(Board targetBoard) {
        return "";
    }
}
