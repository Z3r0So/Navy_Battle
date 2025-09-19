package Attacks;

import Model.Board.Board;

public abstract class Attack {
    protected int row;
    protected int column;

    public Attack(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }
    public abstract String apply(Board targetBoard);

}
